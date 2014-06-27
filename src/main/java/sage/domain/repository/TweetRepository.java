package sage.domain.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import sage.domain.Edge;
import sage.entity.Tag;
import sage.entity.Tweet;

@Repository
public class TweetRepository extends BaseRepository<Tweet> {
  private static final int MAX_RESULTS = 20;

  public List<Tweet> byTag(Tag tag) {
    return byTag(tag, Edge.none());
  }
  
  public List<Tweet> byTag(Tag tag, Edge edge) {
    Collection<Tag> qtags = TagRepository.getQueryTags(tag);
    return byTags(qtags, edge);
  }

  List<Tweet> byTags(Collection<Tag> tags, Edge edge) {
    if (tags.isEmpty()) {
      return new LinkedList<>();
    }
    tags = TagRepository.getQueryTags(tags);
    String q = "select t from Tweet t join t.tags ta where ta in :tags";
    return enhanceQuery(q, edge)
        .setParameterList("tags", tags)
        .setMaxResults(MAX_RESULTS)
        .list();
  }

  public List<Tweet> byAuthor(long authorId) {
    return byAuthor(authorId, Edge.none());
  }

  public List<Tweet> byAuthor(long authorId, Edge edge) {
    String q = "from Tweet t where t.author.id = :authorId";
    return enhanceQuery(q, edge)
        .setLong("authorId", authorId)
        .list();
  }

  public int countByAuthor(long authorId) {
    return (int) (long) session().createQuery(
        "select count(*) from Tweet t where t.author.id = :authorId")
        .setLong("authorId", authorId)
        .uniqueResult();
  }

  public List<Tweet> byAuthorAndTags(long authorId, Collection<Tag> tags) {
    return byAuthorAndTags(authorId, tags, Edge.none());
  }

  public List<Tweet> byAuthorAndTags(long authorId, Collection<Tag> tags, Edge edge) {
    if (tags.isEmpty()) {
      return new LinkedList<>();
    }
    if (hasRoot(tags)) {
      return byAuthor(authorId);
    }
    tags = TagRepository.getQueryTags(tags);
    String q = "select t from Tweet t join t.tags ta where t.author.id=:authorId and ta in :tags";
    return enhanceQuery(q, edge)
        .setLong("authorId", authorId)
        .setParameterList("tags", tags)
        .list();
  }

  public List<Tweet> connectTweets(long blogId) {
    Query queryShares = session().createQuery(
        "from Tweet t where t.blogId = :bid")
        .setLong("bid", blogId);
    List<Tweet> shares = queryShares.list();

    List<Tweet> connected = new ArrayList<>(shares);

    if (shares.size() > 0) {
      Set<Long> originIds = new HashSet<>();
      for (Tweet origin : shares) {
        originIds.add(origin.getId());
      }
      Query queryReshares = session().createQuery(
          "from Tweet t where t.origin.id in :ids")
          .setParameterList("ids", originIds);
      connected.addAll(queryReshares.list());
    }

    return connected;
  }

  public List<Tweet> byOrigin(long originId) {
    return session().createQuery(
        "from Tweet t where t.origin.id = :originId")
        .setLong("originId", originId)
        .list();
  }

  public long forwardCount(long originId) {
    Query query = session().createQuery(
        "select count(*) from Tweet t  where t.origin.id = :originId")
        .setLong("originId", originId);
    return (long) query.uniqueResult();
  }

  private Query enhanceQuery(String q, Edge edge) {
    switch (edge.type) {
    case NONE:
      q += " order by t.id desc";
      return session().createQuery(q).setMaxResults(MAX_RESULTS);

    case BEFORE:
      q += " and t.id < :beforeId";
      q += " order by t.id desc";
      return session().createQuery(q).setLong("beforeId", edge.edgeId).setMaxResults(MAX_RESULTS);

    case AFTER:
      q += " and t.id > :afterId";
      q += " order by t.id desc";
      return session().createQuery(q).setLong("afterId", edge.edgeId).setMaxResults(MAX_RESULTS);

    default:
      throw new UnsupportedOperationException();
    }
  }

  private boolean hasRoot(Collection<Tag> tags) {
    for (Tag tag : tags) {
      if (tag.getId() == Tag.ROOT_ID) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected Class<Tweet> entityClass() {
    return Tweet.class;
  }
}
