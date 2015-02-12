package sage.web.context;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticPathExposer {
  public static final String RS = "/rs";

  @Autowired
  private ServletContext servletContext;

  @PostConstruct
  public void init() {
    servletContext.setAttribute("rs", RS);
  }

}
