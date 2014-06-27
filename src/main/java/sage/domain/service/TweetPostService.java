package sage.domain.service;

import httl.util.StringUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.Constants;
import sage.domain.repository.CommentRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.TweetRepository;
import sage.domain.repository.UserRepository;
import sage.domain.search.SearchBase;
import sage.entity.Blog;
import sage.entity.Comment;
import sage.entity.Tweet;
import sage.entity.User;
import sage.transfer.TweetCard;

@Service
@Transactional
public class TweetPostService {
  @Autowired
  private SearchBase searchBase;
  @Autowired
  private TransferService transfers;
  @Autowired
  private NotifService notifService;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TweetRepository tweetRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private CommentRepository commentRepo;

  public Tweet newTweet(long userId, String content, Collection<Long> tagIds) {
    ParsedContent parsedContent = processContent(content);
    content = parsedContent.content;
    
    Tweet tweet = new Tweet(content, userRepo.load(userId), new Date(),
        tagRepo.byIds(tagIds));
    tweetRepo.save(tweet);
    
    for (Long mentioned : parsedContent.mentionedIds) {
      notifService.mentionedByTweet(mentioned, userId, tweet.getId());
    }
    
    searchBase.index(tweet.getId(), transfers.toTweetCardNoCount(tweet));
    return tweet;
  }

  public Tweet forward(long userId, String content, long originId) {
    ParsedContent parsedContent = processContent(content);
    content = parsedContent.content;
    
    Tweet origin = tweetRepo.load(originId);
    Tweet tweet;
    Deque<Tweet> furtherOrigins = null;
    if (origin.getOrigin() == null) {
      tweet = new Tweet(content, userRepo.load(userId), new Date(), origin);
    }
    else {
      furtherOrigins = furtherOrigins(origin);
      Tweet initialOrigin = furtherOrigins.getLast();
      tweet = new Tweet(content, userRepo.load(userId), new Date(), initialOrigin, enPreforw(origin));
    }
    tweetRepo.save(tweet);
    
    notifService.forwarded(origin.getAuthor().getId(), userId, tweet.getId());
    if (furtherOrigins != null) {
      for (Tweet eachOrigin : furtherOrigins) {
        notifService.forwarded(eachOrigin.getAuthor().getId(), userId, tweet.getId());
      }
    }
    
    for (Long mentioned : parsedContent.mentionedIds) {
      notifService.mentionedByTweet(mentioned, userId, tweet.getId());
    }
    
    searchBase.index(tweet.getId(), transfers.toTweetCardNoCount(tweet));
    return tweet;
  }

  public Comment comment(long userId, String content, long sourceId) {
    ParsedContent parsedContent = processContent(content);
    content = parsedContent.content;
    
    Tweet source = tweetRepo.load(sourceId);
    Comment comment = new Comment(content, userRepo.load(userId),
        new Date(), source);
    commentRepo.save(comment);
    
    notifService.commented(source.getAuthor().getId(), userId, comment.getId());
    for (Long mentioned : parsedContent.mentionedIds) {
      notifService.mentionedByComment(mentioned, userId, comment.getId());
    }
    return comment;
  }

  public void share(long userId, String content, String sourceUrl) {
    // XXX
  }

  public void share(long userId, Blog blog) {
    final int SUM_LEN = 100;
    String content = blog.getContent();
    String summary = content.length() > SUM_LEN ? content.substring(0, SUM_LEN) : content;
    Tweet tweet = new Tweet(
        "发表了博客：[" + blogRef(blog) + "] " + summary,
        userRepo.load(userId),
        new Date(),
        blog);
    tweetRepo.save(tweet);
    
    searchBase.index(tweet.getId(), transfers.toTweetCardNoCount(tweet));
  }

  public boolean delete(long userId, long tweetId) {
    Tweet tweet = tweetRepo.get(tweetId);
    if (tweet == null) {
      return false;
    }
    
    if (userId == tweet.getAuthor().getId()) {
      tweetRepo.delete(tweet);
      searchBase.delete(TweetCard.class, tweetId);
      return true;
    }
    else
      return false;
  }

  private String blogRef(Blog blog) {
    return String.format("<a href=\"%s\">%s</a>",
        Constants.WEB_CONTEXT_ROOT + "/blog/" + blog.getId(), blog.getTitle());
  }

  /*
   * Find the further origins of this origin
   */
  private Deque<Tweet> furtherOrigins(Tweet origin) {
    Deque<Tweet> origins = new ArrayDeque<>();
    origin = origin.getOrigin();
    while (origin != null) {
      origins.add(origin);
      origin = origin.getOrigin();
    }
    return origins;
  }

  private String enPreforw(Tweet tweet) {
    String asPreforw = " ||@" + tweet.getAuthor().getName() + "#" + tweet.getAuthor().getId() + " : "
        + tweet.getContent();
    if (tweet.getPreforw() == null)
      return asPreforw;
    else
      return asPreforw + tweet.getPreforw();
  }

  /*
   * Escape HTML and replace mentions
   */
  private ParsedContent processContent(String content) {
    content = StringUtils.escapeXml(content);
    Set<Long> mentionedIds = new HashSet<>();
    content = replaceMention(content, 0, new StringBuilder(), mentionedIds);
    
    return new ParsedContent(content, mentionedIds);
  }

  /*
   * Replace "@xxx" mentions recursively
   */
  public String replaceMention(String content, int startIndex, StringBuilder sb, Set<Long> mentionedIds) {
    int indexOfAt = content.indexOf('@', startIndex);
    int indexOfSpace = content.indexOf(' ', indexOfAt);
    int indexOfInnerAt = content.lastIndexOf('@', indexOfSpace - 1);

    if (indexOfAt >= 0 && indexOfSpace >= 0) {
      if (indexOfInnerAt > indexOfAt && indexOfInnerAt < indexOfSpace) {
        indexOfAt = indexOfInnerAt;
      }
      String name = content.substring(indexOfAt + 1, indexOfSpace);
      User user = userRepo.findByName(name);
      
      if (user != null) {
        // A valid mention
        mentionedIds.add(user.getId());
        sb.append(content.substring(startIndex, indexOfAt)).append('@').append(name)
            .append('#').append(user.getId());
        return replaceMention(content, indexOfSpace, sb, mentionedIds);
      }
      else {
        if (startIndex == 0) {
          return content;
        }
        sb.append(content.substring(indexOfAt, indexOfSpace));
        return replaceMention(content, indexOfSpace, sb, mentionedIds);
      }
    }

    // Exit
    if (startIndex == 0) {
      return content;
    }
    return sb.append(content.substring(startIndex)).toString();
  }
  
  private static class ParsedContent {
    final String content;
    final Set<Long> mentionedIds;
    
    ParsedContent(String content, Set<Long> mentionedIds) {
      this.content = content;
      this.mentionedIds = mentionedIds;
    }
  }
}
