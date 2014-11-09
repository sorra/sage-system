package sage.domain.repository;

import org.springframework.stereotype.Repository;
import sage.entity.TagChangeRequest;

@Repository
public class TagChangeRequestRepository extends BaseRepository<TagChangeRequest> {
  @Override
  protected Class<TagChangeRequest> entityClass() {
    return TagChangeRequest.class;
  }
}
