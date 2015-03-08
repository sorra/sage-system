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
    Set<TweetCard> mergedSet = new HashSet<>();
    // TweetsByFollowings must be added first, they are prior
    mergedSet.addAll(transfers.toTweetCards(tweetRead.byFollowings(userId, edge)));
    mergedSet.addAll(tcsByTagHeeds(userId, edge));
    // TODO Receive from group heeds
    mergedSet.addAll(tcsByFollowListHeeds(userId, edge));
    
    return new Stream(higherSort(naiveSortTC(mergedSet)));
  }

  private List<TweetCard> tcsByTagHeeds(long userId, Edge edge) {
    List<TweetCard> tcsByTags = new ArrayList<>();
    for (TagHeed hd : heed.tagHeeds(userId)) {
      Long tagId = hd.getTag().getId();
      List<TweetCard> tagTcs = transfers.toTweetCards(tweetRead.byTag(tagId, edge));
      for (TweetCard t : tagTcs) {
        t.beFromTag(tagId);
      }
      tcsByTags.addAll(tagTcs);
    }
    return tcsByTags;
  }

  private List<TweetCard> tcsByFollowListHeeds(long userId, Edge edge) {
    List<TweetCard> tcsByFollowLists = new ArrayList<>();
    for (FollowListHeed hd : heed.followListHeeds(userId)) {
      FollowListLite followList = FollowListLite.fromEntity(hd.getList());
      tcsByFollowLists.addAll(transfers.toTweetCards(tweetRead.byFollowListLite(followList, edge)));
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

  private List<TweetCard> naiveSort(Collection<Tweet> tweets) {
    List<TweetCard> tcs = transfers.toTweetCards(tweets);
    Collections.sort(tcs, Comparators.tweetCardOnId);
    return tcs;
  }
  
  private List<TweetCard> naiveSortTC(Collection<TweetCard> tcs) {
    List<TweetCard> tcList = new ArrayList<>(tcs);
    Collections.sort(tcList, Comparators.tweetCardOnId);
    return tcList;
  }

  private List<Item> higherSort(List<TweetCard> tcs) {
    // TODO Pull-near
    return combine(tcs);
  }

  private List<Item> combine(List<TweetCard> tcs) {
    List<CombineGroup> groupSeq = new ArrayList<>();
    for (TweetCard tc : tcs) {
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
