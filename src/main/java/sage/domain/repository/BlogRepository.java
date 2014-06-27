package sage.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import sage.entity.Blog;
import sage.entity.Tag;

@Repository
public class BlogRepository extends BaseRepository<Blog> {
  private static final int MAX_RESULTS = 20;

  public List<Blog> all() {
    return session().createQuery("from Blog b")
        .setMaxResults(MAX_RESULTS)
        .list();
  }

  public List<Blog> byTags(Collection<Tag> tags) {
    tags = TagRepository.getQueryTags(tags);
    return session().createQuery(
        "select b from Blog b join b.tags ta where ta in :tags")
        .setParameterList("tags", tags)
        .setMaxResults(MAX_RESULTS)
        .list();
  }

  public List<Blog> byAuthor(long authorId) {
    return session().createQuery(
        "from Blog b where b.author.id = :authorId")
        .setLong("authorId", authorId)
        .list();
  }

  public int countByAuthor(long authorId) {
    return (int) (long) session().createQuery(
        "select count(*) from Blog b where b.author.id = :authorId")
        .setLong("authorId", authorId)
        .uniqueResult();
  }

  @Override
  protected Class<Blog> entityClass() {
    return Blog.class;
  }

}
