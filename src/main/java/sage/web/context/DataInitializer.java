package sage.web.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import sage.domain.service.BlogPostService;
import sage.domain.service.ServiceInitializer;
import sage.domain.service.TagService;
import sage.domain.service.TweetPostService;
import sage.entity.Blog;
import sage.entity.Tag;

@Component
public class DataInitializer {
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private ServiceInitializer na;
  @Autowired
  private WebApplicationContext wac;
  @Autowired
  private TagService tagService;
  @Autowired
  private BlogPostService blogPostService;
  @Autowired
  private TweetPostService tweetPostService;

  @PostConstruct
  public void init() {
    String docRootPath = wac.getServletContext().getRealPath("/docs");
    logger.info("Reading docRootPath: " + docRootPath);
    File docFolder = new File(docRootPath);
    Assert.isTrue(docFolder.exists());
    for (File doc : docFolder.listFiles()) {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(doc)));) {
        loadDoc(br);
      }
      catch (IOException e) {
        logger.error("Fail at file: " + doc.getAbsolutePath(), e);
      }
    }
  }

  private void loadDoc(BufferedReader br) throws IOException {

    String tags = br.readLine();
    String title = br.readLine();
    if (tags == null || title == null) {
      logger.error("File: {}, please check its data!");
      return;
    }
    StringBuilder contentBuilder = new StringBuilder();
    String line = "";
    while (line != null) {
      line = br.readLine();
      if (line != null) {
        contentBuilder.append(line).append('\n');
      }
    }
    String content = contentBuilder.toString();

    String[] tagNames = tags.split(" ");
    Collection<Long> tagIds = new ArrayList<>();
    for (String tagName : tagNames) {
      for (Tag tag : tagService.getTagsByName(tagName)) {
        tagIds.add(tag.getId());
      }
    }
    long uid = Math.abs(new Random().nextLong()) % 3 + 1;
    logger.info("###{} ###author: {}", title, uid);
    Blog blog = blogPostService.newBlog(uid, title, content, tagIds);
    tweetPostService.share(uid, blog);
  }
}
