package sage.domain.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.TagChangeRequestRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.UserRepository;
import sage.entity.Tag;
import sage.entity.TagChangeRequest;
import sage.util.Colls;

@Service
@Transactional
public class TagChangeService {
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private TagChangeRequestRepository requestRepo;
  @Autowired
  private UserRepository userRepo;

  public Long newTag(String name, long parentId, String intro) {
    Tag tag = new Tag(name, tagRepo.load(parentId), intro);
    if (tagRepo.byNameAndParent(name, parentId) == null) {
      tagRepo.save(tag);
      return tag.getId();
    }
    else
      return null;
  }

  public TagChangeRequest requestMove(Long userId, Long tagId, Long parentId) {
    return requestRepo.save(TagChangeRequest.forMove(tagRepo.load(tagId), userRepo.load(userId), parentId));
  }

  public TagChangeRequest requestSetIntro(Long userId, Long tagId, String intro) {
    return requestRepo.save(TagChangeRequest.forSetIntro(tagRepo.load(tagId), userRepo.load(userId), intro));
  }

  public Collection<TagChangeRequest> getRequestsOfTag(long tagId) {
    return Colls.copy(requestRepo.byTag(tagId));
  }

  public Collection<TagChangeRequest> getRequestsOfTagScope(long tagId) {
    return Colls.copy(requestRepo.byTagScope(tagRepo.get(tagId)));
  }

  public void cancelRequest(Long userId, Long reqId) {
    TagChangeRequest request = requestRepo.get(reqId);
    if (!IdCommons.equal(request.getSubmitter().getId(), userId)) {
      throw new DomainRuntimeException("User[%d] is not the owner of TagChangeRequest[%d]", userId, reqId);
    }
    request.setStatus(TagChangeRequest.Status.CANCELED);
  }

  public void acceptRequest(Long userId, Long reqId) {
    transactRequest(userId, reqId, TagChangeRequest.Status.ACCEPTED);
  }

  public void rejectRequest(Long userId, Long reqId) {
    transactRequest(userId, reqId, TagChangeRequest.Status.REJECTED);
  }

  private void transactRequest(Long userId, Long reqId, TagChangeRequest.Status status) {
    //TODO Check admin permission
    TagChangeRequest request = requestRepo.get(reqId);
    request.setStatus(status);
    request.setTransactor(userRepo.load(userId));

    if (status == TagChangeRequest.Status.ACCEPTED) {
      if (request.getType() == TagChangeRequest.Type.MOVE) {
        doMove(request.getTag().getId(), request.getParentId());
      } else if (request.getType() == TagChangeRequest.Type.SET_INTRO) {
        doSetIntro(request.getTag().getId(), request.getIntro());
      }
    }
  }

  public void doMove(long tagId, long parentId) {
    tagRepo.get(tagId).setParent(tagRepo.load(parentId));
  }

  public void doSetIntro(long tagId, String intro) {
    tagRepo.get(tagId).setIntro(intro);
  }

}
