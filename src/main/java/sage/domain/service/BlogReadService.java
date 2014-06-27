package sage.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.Edge;
import sage.domain.repository.BlogRepository;
import sage.domain.repository.FollowRepository;
import sage.entity.Blog;
import sage.entity.Follow;
import sage.transfer.BlogData;

@Service
@Transactional(readOnly = true)
public class BlogReadService {
  @Autowired
  private BlogRepository blogRepo;
  @Autowired
  private FollowRepository followRepo;

  private static final int MIN_LIST_SIZE = 20;

  /**
   * @return blogData | null
   */
  public BlogData getBlogData(long blogId) {
    Blog blog = blogRepo.get(blogId);
    return blog == null ? null : new BlogData(blog);
  }

  public List<BlogData> getAllBlogDatas() {
    return listBlogDatas(blogRepo.all(), true);
  }

  public List<BlogData> blogStream(long userId, Edge edge) {
    List<Blog> blogs = new ArrayList<>();
    // TODO also use tags
    for (Follow follow : followRepo.followings(userId)) {
      List<Blog> results = blogRepo.byAuthor(follow.getTarget().getId());
      blogs.addAll(results);
    }
    return listBlogDatas(blogs, false);
  }

  public List<BlogData> byAuthor(long authorId) {
    return listBlogDatas(blogRepo.byAuthor(authorId), true);
  }

  private List<BlogData> listBlogDatas(List<Blog> blogs, boolean eagerCopy) {
    if (eagerCopy) {
      blogs = new ArrayList<>(blogs);
    }
    List<BlogData> bds = new ArrayList<>(MIN_LIST_SIZE);
    for (Blog b : blogs) {
      bds.add(new BlogData(b));
    }
    return bds;
  }
}
