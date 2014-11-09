package sage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class TagChangeRequest {
  private Long id;
  private Tag tag;
  private User submitter;
  private User transactor;
  private Status status = Status.PENDING;
  private Type type;
  private Long parentId;
  private String intro;

  public static TagChangeRequest forMove(Tag tag, User submitter, Long parentId) {
    return new TagChangeRequest(tag, submitter, Type.MOVE, parentId, null);
  }

  public static TagChangeRequest forSetIntro(Tag tag, User submitter, String intro) {
    return new TagChangeRequest(tag, submitter, Type.SET_INTRO, null, intro);
  }

  TagChangeRequest() {}

  public TagChangeRequest(Tag tag, User submitter, Type type, Long parentId, String intro) {
    this.tag = tag;
    this.submitter = submitter;
    this.type = type;
    this.parentId = parentId;
    this.intro = intro;
  }

  public static enum Status {
    PENDING, CANCELED, ACCEPTED, REJECTED
  }
  public static enum Type {
    MOVE, SET_INTRO
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  @OneToOne
  public Tag getTag() {
    return tag;
  }
  void setTag(Tag tag) {
    this.tag = tag;
  }

  @OneToOne
  public User getSubmitter() {
    return submitter;
  }
  void setSubmitter(User submitter) {
    this.submitter = submitter;
  }

  @OneToOne
  public User getTransactor() {
    return transactor;
  }
  public void setTransactor(User transactor) {
    this.transactor = transactor;
  }

  public Status getStatus() {
    return status;
  }
  public void setStatus(Status status) {
    this.status = status;
  }

  public Type getType() {
    return type;
  }
  void setType(Type type) {
    this.type = type;
  }

  public Long getParentId() {
    return parentId;
  }
  void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getIntro() {
    return intro;
  }
  public void setIntro(String intro) {
    this.intro = intro;
  }
}
