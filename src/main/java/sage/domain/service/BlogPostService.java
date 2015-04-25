package sage.domain.service;

import java.util.Collection;
import java.util.Date;

import httl.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.commons.BadArgumentException;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.commons.IdCommons;
import sage.domain.repository.BlogRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.UserRepository;
import sage.domain.search.SearchBase;
import sage.entity.Blog;
import sage.transfer.BlogView;

@Service
@Transactional
public class BlogPostService {
  private static final int BLOG_TITLE_MAX_LEN = 100, BLOG_CONTENT_MAX_LEN = 10000;
  private static final BadArgumentException BAD_INPUT_LENGTH = new BadArgumentException(
      "输入长度不正确(标题1~100字,内容1~10000字)");

  @Autowired
  private SearchBase searchBase;
  @Autowired
  private BlogRepository blogRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TagRepository tagRepo;

  public Blog post(long userId, String title, String content, Collection<Long> tagIds) {
    checkLength(title, content);
    Blog blog = new Blog(title, content, userRepo.load(userId), new Date(), tagRepo.byIds(tagIds));
    escapeAndSetText(blog);

    blogRepo.save(blog);
    searchBase.index(blog.getId(), new BlogView(blog));
    return blog;
  }

  public Blog edit(long userId, long blogId, String title, String content, Collection<Long> tagIds) {
    checkLength(title, content);
    Blog blog = blogRepo.get(blogId);
    if (!IdCommons.equal(blog.getAuthor().getId(), userId)) {
      throw new DomainRuntimeException("User[%d] is not the author of Blog[%d]", userId, blogId);
    }
    blog.setTitle(title);
    blog.setContent(content);
    blog.setModifiedTime(new Date());
    blog.setTags(tagRepo.byIds(tagIds));
    escapeAndSetText(blog);

    blogRepo.update(blog);
    searchBase.index(blog.getId(), new BlogView(blog));
    return blog;
  }

  public void delete(long userId, long blogId) {
    Blog blog = blogRepo.get(blogId);
    if (!IdCommons.equal(blog.getAuthor().getId(), userId)) {
      throw new DomainRuntimeException("User[%d] is not the author of Blog[%d]", userId, blogId);
    }
    blogRepo.delete(blog);
    searchBase.delete(BlogView.class, blog.getId());
  }

  private void checkLength(String title, String content) {
    if (title.isEmpty() || title.length() > BLOG_TITLE_MAX_LEN
        || content.isEmpty() || content.length() > BLOG_CONTENT_MAX_LEN) {
      throw BAD_INPUT_LENGTH;
    }
  }

  private void escapeAndSetText(Blog blog) {
    String title = StringUtils.escapeXml(blog.getTitle());
    String content = StringUtils.escapeXml(blog.getContent()).replace("\n", "  \n");
    blog.setTitle(title);
    blog.setContent(content);
  }
}
