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
  private String name;
  private String intro;

  public static TagChangeRequest forMove(Tag tag, User submitter, Long parentId) {
    TagChangeRequest request = new TagChangeRequest(tag, submitter, Type.MOVE);
    request.setParentId(parentId);
    return request;
  }

  public static TagChangeRequest forRename(Tag tag, User submitter, String name) {
    TagChangeRequest request = new TagChangeRequest(tag, submitter, Type.RENAME);
    request.setName(name);
    return request;
  }

  public static TagChangeRequest forSetIntro(Tag tag, User submitter, String intro) {
    TagChangeRequest request = new TagChangeRequest(tag, submitter, Type.SET_INTRO);
    request.setIntro(intro);
    return request;
  }

  TagChangeRequest() {}

  public TagChangeRequest(Tag tag, User submitter, Type type) {
    this.tag = tag;
    this.submitter = submitter;
    this.type = type;
  }

  public enum Status {
    PENDING, CANCELED, ACCEPTED, REJECTED
  }
  public enum Type {
    MOVE, RENAME, SET_INTRO
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

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getIntro() {
    return intro;
  }
  public void setIntro(String intro) {
    this.intro = intro;
  }
}
