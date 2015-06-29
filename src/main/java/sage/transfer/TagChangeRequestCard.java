package sage.transfer;

import sage.entity.TagChangeRequest;

public class TagChangeRequestCard {
  private Long id;
  private TagLabel tag;
  private UserLabel submitter;
  private UserLabel transactor;
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
    status = req.getStatus().name();
    type = req.getStatus().name();
    switch (req.getType()) {
      case MOVE:
        desc = "移动到:标签编号"+req.getParentId()+"下";
        break;
      case RENAME:
        desc = "改名为\""+req.getName()+'"';
        break;
      case SET_INTRO:
        desc = "修改介绍\""+req.getIntro()+'"';
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
