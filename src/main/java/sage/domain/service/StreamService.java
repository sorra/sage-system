package sage.domain.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import sage.domain.commons.Comparators;
import sage.domain.commons.Edge;
import sage.domain.commons.IdCommons;
import sage.entity.FollowListHeed;
import sage.entity.TagHeed;
import sage.entity.Tweet;
import sage.transfer.*;
import sage.util.Colls;

@Service
public class StreamService {
  @Autowired
  private TweetReadService tweetRead;
  @Autowired
  private TransferService transfers;
  @Autowired
  private HeedService heed;

  public Stream istream(long userId) {
    return istream(userId, Edge.none());
  }

  public Stream istream(long userId, Edge edge) {
    // Deduplicate
    Set<TweetView> mergedSet = new HashSet<>();
    // TweetsByFollowings must be added first, they are prior
    mergedSet.addAll(transfers.toTweetViews(tweetRead.byFollowings(userId, edge)));
    mergedSet.addAll(tcsByTagHeeds(userId, edge));
    // TODO Receive from group heeds
    mergedSet.addAll(tcsByFollowListHeeds(userId, edge));
    
    return new Stream(higherSort(naiveSortTC(mergedSet)));
  }

  private List<TweetView> tcsByTagHeeds(long userId, Edge edge) {
    List<TweetView> tcsByTags = new ArrayList<>();
    for (TagHeed hd : heed.tagHeeds(userId)) {
      Long tagId = hd.getTag().getId();
      List<TweetView> tagTcs = transfers.toTweetViews(tweetRead.byTag(tagId, edge));
      for (TweetView t : tagTcs) {
        t.beFromTag(tagId);
      }
      tcsByTags.addAll(tagTcs);
    }
    return tcsByTags;
  }

  private List<TweetView> tcsByFollowListHeeds(long userId, Edge edge) {
    List<TweetView> tcsByFollowLists = new ArrayList<>();
    for (FollowListHeed hd : heed.followListHeeds(userId)) {
      FollowListLite followList = FollowListLite.fromEntity(hd.getList());
      tcsByFollowLists.addAll(transfers.toTweetViews(tweetRead.byFollowListLite(followList, edge)));
    }
    return tcsByFollowLists;
  }

  public Stream tagStream(long tagId, Edge edge) {
    List<Tweet> tweets = tweetRead.byTag(tagId, edge);
    return new Stream(naiveSort(tweets));
  }

  public Stream personalStream(long userId, Edge edge) {
    List<Tweet> tweets = tweetRead.byAuthor(userId, edge);
    return new Stream(naiveSort(tweets));
  }

  public Stream groupStream(long groupId, Edge edge) {
    //TODO group stream
    List<Tweet> tweets = null;
    return new Stream(naiveSort(tweets));
  }

  private List<TweetView> naiveSort(Collection<Tweet> tweets) {
    List<TweetView> tcs = transfers.toTweetViews(tweets);
    Collections.sort(tcs, Comparators.tweetViewNewerFirst);
    return tcs;
  }
  
  private List<TweetView> naiveSortTC(Collection<TweetView> tcs) {
    return Colls.copySort(Comparators.tweetViewNewerFirst, tcs);
  }

  private List<Item> higherSort(List<TweetView> tcs) {
    // TODO Pull-near
    return combine(tcs);
  }

  private List<Item> combine(List<TweetView> tcs) {
    List<CombineGroup> groupSeq = new ArrayList<>();
    for (TweetView tc : tcs) {
      if (tc.getOrigin() != null) {
        long originId = tc.getOrigin().getId();
        CombineGroup foundGroup = findInSeq(originId, groupSeq);
        if (foundGroup != null) {
          foundGroup.addForward(tc);
        }
        else {
          groupSeq.add(CombineGroup.newByFirst(tc));
        }
      }
      else {
        CombineGroup foundGroup = findInSeq(tc.getId(), groupSeq);
        if (foundGroup != null) {
          foundGroup.addOrigin(tc);
        }
        else {
          groupSeq.add(CombineGroup.newByOrigin(tc));
        }
      }
    }
  
    List<Item> sequence = new ArrayList<>(groupSeq.size());
    for (CombineGroup group : groupSeq) {
      if (group.getForwards().isEmpty()) {
        Assert.notNull(group.getOrigin());
        sequence.add(group.getOrigin());
      }
      else
        sequence.add(group);
    }
    return sequence;
  }

  private CombineGroup findInSeq(long id, List<CombineGroup> groupSequence) {
    for (CombineGroup group : groupSequence) {
      if (IdCommons.equal(group.getOrigin().getId(), id)) {
        return group;
      }
    }
    return null;
  }
}
