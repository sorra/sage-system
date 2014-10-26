package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.TagHeed;

@Repository
public class TagHeedRepository extends BaseRepository<TagHeed> {

  public TagHeed find(long userId, long tagId) {
    return (TagHeed) session().createQuery("from TagHeed ht where ht.user.id=:userId and ht.tag.id=:tagId")
        .setLong("userId", userId).setLong("tagId", tagId).uniqueResult();
  }
  
  public List<TagHeed> findByUser(long userId) {
    return session().createQuery("from TagHeed ht where ht.user.id=:userId")
        .setLong("userId", userId).list();
  }

  @Override
  protected Class<TagHeed> entityClass() {
    return TagHeed.class;
  }

}
