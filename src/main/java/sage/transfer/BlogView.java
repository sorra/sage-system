package sage.transfer;

import java.util.Date;
import java.util.List;

import sage.entity.Blog;
import sage.entity.User;
import sage.util.Colls;

public class BlogView {

  private Long id;
  private UserLabel author;
  private String title;
  private String content;
  private Date createdTime;
  private Date modifiedTime;
  private List<TagLabel> tags;
  
  BlogView() {}

  public BlogView(Blog blog) {
    id = blog.getId();
    author = new UserLabel(blog.getAuthor());

    title = blog.getTitle();
    content = blog.getContent();
    createdTime = blog.getCreatedTime();
    modifiedTime = blog.getModifiedTime();

    tags = Colls.map(blog.getTags(), TagLabel::new);
  }

  public Long getId() {
    return id;
  }

  public UserLabel getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public Date getModifiedTime() {
    return modifiedTime;
  }

  public List<TagLabel> getTags() {
    return tags;
  }
}
