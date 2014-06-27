package sage.domain.repository.nosql;

import org.springframework.stereotype.Repository;

import sage.entity.nosql.FollowCatalog;

@Repository
public class FollowCatalogRepository extends BaseCouchbaseRepository<FollowCatalog> {

  @Override
  protected Class<FollowCatalog> entityClass() {
    return FollowCatalog.class;
  }
  
}
