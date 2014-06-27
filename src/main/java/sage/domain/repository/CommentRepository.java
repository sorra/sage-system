package sage.domain.repository;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import sage.entity.Comment;

@Repository
public class CommentRepository extends BaseRepository<Comment> {

  public List<Comment> bySource(long sourceId) {
    Query query = session().createQuery(
        "from Comment c where c.source.id = :sourceId")
        .setLong("sourceId", sourceId);
    return query.list();
  }

  public long commentCount(long sourceId) {
    Query query = session().createQuery(
        "select count(*) from Comment c where c.source.id = :sourceId")
        .setLong("sourceId", sourceId);
    return (long) query.uniqueResult();
  }

  @Override
  protected Class<Comment> entityClass() {
    return Comment.class;
  }

}
