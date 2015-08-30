package sage.domain.service;

import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import httl.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.commons.ReplaceMention;
import sage.domain.repository.*;
import sage.entity.Blog;
import sage.entity.TopicPost;
import sage.entity.TopicReply;
import sage.transfer.TopicPreview;
import sage.util.Colls;

@Service
@Transactional
public class TopicService {
  @Autowired
  private NotifService notifService;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private GroupRepository groupRepo;
  @Autowired
  private TopicPostRepository topicPostRepo;
  @Autowired
  private BlogRepository blogRepo;
  @Autowired
  private TopicReplyRepository topicReplyRepo;

  public TopicPost post(long userId, Blog blog, long groupId) {
    if (!IdCommons.equal(userId, blog.getAuthor().getId())) {
      throw new DomainRuntimeException("Cannot post topic. User[%d] is not the author of Blog[%d]", userId, blog.getId());
    }
    return topicPostRepo.save(new TopicPost(blog, groupRepo.load(groupId)));
  }

  public TopicPost post(long userId, long blogId, long groupId) {
    return post(userId, blogRepo.nonNull(blogId), groupId);
  }

  public TopicReply reply(long userId, String content, long topicPostId, Long toReplyId) {
    content = StringUtils.escapeXml(content);
    Set<Long> mentionedIds = new HashSet<>();
    content = ReplaceMention.with(userRepo).apply(content, mentionedIds);

    Long toUserId = Optional.ofNullable(toReplyId)
        .map(id -> topicReplyRepo.get(id).getAuthor().getId()).orElse(null);
    TopicReply reply = topicReplyRepo.save(
        new TopicReply(topicPostRepo.load(topicPostId), userRepo.load(userId), new Date(), content)
            .setToInfo(toUserId, toReplyId));

    mentionedIds.forEach(atId -> notifService.mentionedByTopicReply(atId, userId, reply.getId()));
    if (reply.getToUserId() != null) {
      notifService.repliedInTopic(toUserId, userId, reply.getId());
    }
    return reply;
  }

  public void setHiddenOfTopicPost(long id, boolean hidden) {
    TopicPost post = topicPostRepo.get(id);
    boolean origHidden = post.isHidden();
    if (origHidden != hidden) {
      post.setHidden(hidden);
      topicPostRepo.update(post);
    }
  }

  public TopicPost getTopicPost(long id) {
    return topicPostRepo.nonNull(id);
  }

  public List<TopicReply> getTopicReplies(long topicPostId) {
    return topicReplyRepo.byTopicPost(topicPostId);
  }

  // 按帖子最后活动时间排序, 新的在前
  public Collection<TopicPreview> groupTopicPreviews(long groupId) {
    List<TopicPost> topicPosts = topicPostRepo.byGroup(groupId).stream()
        .map(topicPost -> {
          Date time = topicReplyRepo.theLastByTopicPost(topicPost.getId())
              .map(TopicReply::getTime).orElse(topicPost.getTime());
          if (time == null) time = new Date(0);
          return Pair.of(topicPost, time);
        })
        .sorted(Comparator.comparing(Pair::getRight, Comparator.<Date>reverseOrder()))
        .map(Pair::getLeft).collect(Collectors.toList());
    return Colls.map(topicPosts,
        topicPost -> new TopicPreview(topicPost, getTopicReplies(topicPost.getId()).size()));

  }
}
