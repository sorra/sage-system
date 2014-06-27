package sage.web.context;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sage.domain.Constants;

@Component
public class StaticPathExposer {
  public static final String BASE = Constants.WEB_CONTEXT_ROOT;
  public static final String RS = Constants.WEB_CONTEXT_ROOT + "/rs";

  @Autowired
  private ServletContext servletContext;

  @PostConstruct
  public void init() {
    servletContext.setAttribute("base", BASE);
    servletContext.setAttribute("rs", RS);
  }

}
