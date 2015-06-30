package sage.transfer;

import sage.entity.TagChangeRequest;

public class TagChangeRequestCard {
  private Long id;
  private TagLabel tag;
  private UserLabel submitter;
  private UserLabel transactor;
  private String statusKey;
  private String status;
  private String type;
  private String desc;

  TagChangeRequestCard() {}

  public TagChangeRequestCard(TagChangeRequest req) {
    id = req.getId();
    tag = new TagLabel(req.getTag());
    submitter = new UserLabel(req.getSubmitter());
    if (req.getTransactor() != null) {
      transactor = new UserLabel(req.getTransactor());
    }
    statusKey = req.getStatus().name();
    status = req.getStatus().desc;
    type = req.getType().desc;
    switch (req.getType()) {
      case MOVE:
        desc = "移动到标签编号"+req.getParentId()+"下";
        break;
      case RENAME:
        desc = "改名\""+req.getName()+'"';
        break;
      case SET_INTRO:
        desc = "修改简介\""+req.getIntro()+'"';
        break;
    }
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

  public String getStatusKey() {
    return statusKey;
  }

  public String getStatus() {
    return status;
  }

  public String getType() {
    return type;
  }

  public String getDesc() {
    return desc;
  }
}
