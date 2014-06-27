package sage;

import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sage.domain.service.*;
import sage.transfer.Stream;

public class SceneTest {
    PrintStream out = System.out;
    
    UserService userService;
    RelationService relationService;
    TweetPostService tweetService;
    BlogPostService blogService;
    TagService tagService;
    StreamService streamService;
    
    @Before
    public void setUp() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("root-context.xml");
        userService = ac.getBean(UserService.class);
        relationService = ac.getBean(RelationService.class);
        tweetService = ac.getBean(TweetPostService.class);
        blogService = ac.getBean(BlogPostService.class);
        tagService = ac.getBean(TagService.class);
        streamService = ac.getBean(StreamService.class);
    }
    
    @Test
    public void procedure() {
        Stream s1 = streamService.istream(1);
        Stream s2 = streamService.istream(2);
        Stream s3 = streamService.istream(3);
    }
    
}
