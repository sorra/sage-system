package sage.transfer;

import sage.entity.TagChangeRequest;

public class TagChangeRequestCard {
  private Long id;
  private TagLabel tag;
  private UserLabel submitter;
  private UserLabel transactor;
  private String status;
  private String type;
  private Long newParentId;
  private String newIntro;

  TagChangeRequestCard() {}

  public TagChangeRequestCard(TagChangeRequest req) {
    id = req.getId();
    tag = new TagLabel(req.getTag());
    submitter = new UserLabel(req.getSubmitter());
    if (req.getTransactor() != null) {
      transactor = new UserLabel(req.getTransactor());
    }
    status = req.getStatus().name();
    type = req.getStatus().name();
    newParentId = req.getParentId();
    newIntro = req.getIntro();
  }

  public Long getId() {
    return id;
  }

  public TagLabel getTag() {
    return tag;
  }

  public UserLabel getSubmitter() {
    return submitter;
  }

  public UserLabel getTransactor() {
    return transactor;
  }

  public String getStatus() {
    return status;
  }

  public String getType() {
    return type;
  }

  public Long getNewParentId() {
    return newParentId;
  }

  public String getNewIntro() {
    return newIntro;
  }
}
