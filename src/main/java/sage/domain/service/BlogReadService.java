package sage.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.Edge;
import sage.domain.repository.BlogRepository;
import sage.domain.repository.FollowRepository;
import sage.entity.Blog;
import sage.transfer.BlogView;
import sage.util.Colls;

@Service
@Transactional(readOnly = true)
public class BlogReadService {
  @Autowired
  private BlogRepository blogRepo;
  @Autowired
  private FollowRepository followRepo;

  /**
   * @return blogData | null
   */
  public BlogView getBlogView(long blogId) {
    Blog blog = blogRepo.get(blogId);
    return blog == null ? null : new BlogView(blog);
  }

  public List<BlogView> getAllBlogViews() {
    return toViews(blogRepo.all());
  }

  public List<BlogView> blogStream(long userId, Edge edge) {
    // TODO also use tags
    return Colls.copy(followRepo.followings(userId)).stream()
        .flatMap(f -> blogRepo.byAuthor(f.getTarget().getId()).stream())
        .map(BlogView::new).collect(Collectors.toList());
  }

  public List<BlogView> byAuthor(long authorId) {
    return toViews(blogRepo.byAuthor(authorId));
  }

  /** eagerCopy aims at Hibernate */
  private List<BlogView> toViews(List<Blog> blogs) {
    return Colls.map(Colls.copy(blogs), BlogView::new);
  }
}
