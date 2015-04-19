package sage.transfer;

import java.util.Date;
import java.util.List;

import sage.entity.Blog;
import sage.util.Colls;
import sage.util.Strings;

public class BlogPreview {
  public long id;
  public String title;
  public UserLabel author;
  public String preview;
  public Date createdTime;
  public Date modifiedTime;
  public List<TagLabel> tags;

  BlogPreview() {}

  public BlogPreview(Blog blog) {
    id = blog.getId();
    title = blog.getTitle();
    author = new UserLabel(blog.getAuthor());
    preview = Strings.cut(blog.getContent(), 0, 100);
    createdTime = blog.getCreatedTime();
    modifiedTime = blog.getModifiedTime();
    tags = Colls.map(blog.getTags(), TagLabel::new);
  }
}
