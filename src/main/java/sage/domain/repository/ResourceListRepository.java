package sage.domain.repository;

import org.springframework.stereotype.Repository;
import sage.entity.ResourceListEntity;

@Repository
public class ResourceListRepository extends BaseRepository<ResourceListEntity> {

  @Override
  protected Class<ResourceListEntity> entityClass() {
    return ResourceListEntity.class;
  }
  
}
