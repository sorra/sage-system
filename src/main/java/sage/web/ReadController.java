package sage.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.Edge;
import sage.domain.service.StreamService;
import sage.domain.service.TweetReadService;
import sage.transfer.CommentCard;
import sage.transfer.Stream;
import sage.transfer.TweetCard;
import sage.web.auth.Auth;

import java.util.List;

@RestController
@RequestMapping("/read")
public class ReadController {
  private final static Logger logger = LoggerFactory.getLogger(ReadController.class);
  @Autowired
  private StreamService streamService;
  @Autowired
  private TweetReadService tweetReadService;

  @RequestMapping("/istream")
  public Stream istream(
      @RequestParam(required = false) Long before,
      @RequestParam(required = false) Long after) {
    Long uid = Auth.checkCurrentUid();
    logger.debug("before {}, after {}", before, after);
    return streamService.istream(uid, getEdge(before, after));
  }

  @RequestMapping("/connect/{blogId}")
  public Stream connect(@PathVariable Long blogId) {
    List<TweetCard> tcs = tweetReadService.connectTweets(blogId);
    return new Stream(tcs);
  }

  @RequestMapping("/{tweetId}/comments")
  public List<CommentCard> comments(@PathVariable Long tweetId) {
    return CommentCard.listOf(tweetReadService.getComments(tweetId));
  }

  @RequestMapping("/tag/{id}")
  public Stream tagStream(@PathVariable Long id,
      @RequestParam(required = false) Long before,
      @RequestParam(required = false) Long after) {
    return streamService.tagStream(id, getEdge(before, after));
  }

  @RequestMapping("/u/{id}")
  public Stream personalStream(@PathVariable Long id,
      @RequestParam(required = false) Long before,
      @RequestParam(required = false) Long after) {
    return streamService.personalStream(id, getEdge(before, after));
  }

  @RequestMapping("/group/{id}")
  public Stream groupStream(@PathVariable Long id,
      @RequestParam(required = false) Long before,
      @RequestParam(required = false) Long after) {
    return streamService.groupStream(id, getEdge(before, after));
  }

  private Edge getEdge(Long beforeId, Long afterId) {
    if (beforeId == null && afterId == null) {
      return Edge.none();
    }
    else if (beforeId != null && afterId != null) {
      throw new UnsupportedOperationException();
    }
    else if (beforeId != null) {
      return Edge.before(beforeId);
    }
    else if (afterId != null) {
      return Edge.after(afterId);
    }
    throw new UnsupportedOperationException();
  }
}
