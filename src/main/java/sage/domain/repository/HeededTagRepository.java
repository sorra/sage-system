package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import sage.entity.HeededTag;

@Repository
public class HeededTagRepository extends BaseRepository<HeededTag> {

  public HeededTag find(long userId, long tagId) {
    return (HeededTag) session().createQuery("from HeededTag ht where ht.user.id=:userId and ht.tag.id=:tagId")
        .setLong("userId", userId).setLong("tagId", tagId).uniqueResult();
  }
  
  public List<HeededTag> findByUser(long userId) {
    return session().createQuery("from HeededTag ht where ht.user.id=:userId")
        .setLong("userId", userId).list();
  }

  @Override
  protected Class<HeededTag> entityClass() {
    return HeededTag.class;
  }

}
