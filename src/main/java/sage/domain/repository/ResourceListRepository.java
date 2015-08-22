package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.ResourceListEntity;

@Repository
public class ResourceListRepository extends BaseRepository<ResourceListEntity> {

  public List<ResourceListEntity> byOwner(long ownerId) {
    return session().createQuery("from ResourceListEntity e where e.ownerId = :ownerId")
        .setLong("ownerId", ownerId).list();
  }

  @Override
  protected Class<ResourceListEntity> entityClass() {
    return ResourceListEntity.class;
  }
  
}
