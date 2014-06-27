package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import sage.domain.Comparators;
import sage.domain.Edge;
import sage.entity.HeededTag;
import sage.entity.Tweet;
import sage.transfer.CombineGroup;
import sage.transfer.Item;
import sage.transfer.Stream;
import sage.transfer.TweetCard;

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
    List<TweetCard> tcsByFols = transfers.toTweetCards(tweetRead.byFollowings(userId, edge));
    
    List<TweetCard> tcsByTags = new ArrayList<>();
    for (HeededTag ht : heed.heededTags(userId)) {
      Long tagId = ht.getTag().getId();
      List<TweetCard> tagTcs = transfers.toTweetCards(tweetRead.byTag(tagId, edge));
      for (TweetCard t : tagTcs) {
        t.beFromTag(tagId);
      }
      tcsByTags.addAll(tagTcs);
    }

    //TODO heed groups

    Set<TweetCard> mergedSet = new HashSet<>();
    // TweetsByFollowings must be added first, they are prior
    mergedSet.addAll(tcsByFols);
    mergedSet.addAll(tcsByTags);
    
    return new Stream(higherSort(naiveSortTC(mergedSet)));
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
    List<Item> cleanList = new ArrayList<>();
    cleanList.addAll(tcs);
  
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
      if (group.getOrigin().getId() == id) {
        return group;
      }
    }
    return null;
  }
}
