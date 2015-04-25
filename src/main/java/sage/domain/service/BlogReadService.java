package sage.domain.service;

import java.util.ArrayList;
import java.util.List;

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
  public BlogView getBlogData(long blogId) {
    Blog blog = blogRepo.nullable(blogId);
    return blog == null ? null : new BlogView(blog);
  }

  public List<BlogView> getAllBlogDatas() {
    return listBlogDatas(blogRepo.all(), true);
  }

  public List<BlogView> blogStream(long userId, Edge edge) {
    List<Blog> blogs = new ArrayList<>();
    // TODO also use tags
    blogs = Colls.flatMap(followRepo.followings(userId), f -> blogRepo.byAuthor(f.getTarget().getId()));
    return listBlogDatas(blogs, false);
  }

  public List<BlogView> byAuthor(long authorId) {
    return listBlogDatas(blogRepo.byAuthor(authorId), true);
  }

  /** eagerCopy aims at Hibernate */
  private List<BlogView> listBlogDatas(List<Blog> blogs, boolean eagerCopy) {
    if (eagerCopy) {
      blogs = Colls.copy(blogs);
    }
    return Colls.map(blogs, BlogView::new);
  }
}
