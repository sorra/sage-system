package sage.domain.repository.nosql;

import org.springframework.stereotype.Repository;

import sage.entity.nosql.ResourceCatalog;

@Repository
public class ResourceCatalogRepository extends BaseCouchbaseRepository<ResourceCatalog> {

  @Override
  protected Class<ResourceCatalog> entityClass() {
    return ResourceCatalog.class;
  }
  
}
