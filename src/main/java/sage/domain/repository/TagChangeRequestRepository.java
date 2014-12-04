package sage.domain.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import sage.entity.Tag;
import sage.entity.TagChangeRequest;

@Repository
public class TagChangeRequestRepository extends BaseRepository<TagChangeRequest> {

  public Collection<TagChangeRequest> byTag(long tagId) {
    return session().createQuery("from TagChangeRequest tcr where tcr.tag.id = :tagId")
        .setLong("tagId", tagId).list();
  }

  public Collection<TagChangeRequest> byTagScope(Tag tag) {
    Collection<Tag> tags = TagRepository.getQueryTags(tag);
    return session().createQuery("from TagChangeRequest tcr where tcr.tag in :tags")
        .setParameterList("tags", tags).list();
  }

  @Override
  protected Class<TagChangeRequest> entityClass() {
    return TagChangeRequest.class;
  }
}
