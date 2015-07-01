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

  public Collection<TagChangeRequest> byTagAndStatus(long tagId, TagChangeRequest.Status status) {
    return session().createQuery("from TagChangeRequest tcr where tcr.tag.id = :tagId and status = :status")
        .setLong("tagId", tagId).setInteger("status", status.ordinal()).list();
  }

  public Collection<TagChangeRequest> byTagScope(Tag tag) {
    Collection<Tag> tags = TagRepository.getQueryTags(tag);
    return session().createQuery("from TagChangeRequest tcr where tcr.tag in :tags")
        .setParameterList("tags", tags).list();
  }

  public Collection<TagChangeRequest> byTagScopeAndStatus(Tag tag, TagChangeRequest.Status status) {
    Collection<Tag> tags = TagRepository.getQueryTags(tag);
    return session().createQuery("from TagChangeRequest tcr where tcr.tag in :tags and status = :status")
        .setParameterList("tags", tags).setInteger("status", status.ordinal()).list();
  }

  @Override
  protected Class<TagChangeRequest> entityClass() {
    return TagChangeRequest.class;
  }
}
