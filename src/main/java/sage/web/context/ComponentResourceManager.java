package sage.web.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ComponentResourceManager {
  private static ComponentResourceManager instance;

  @Autowired
  private ServletContext servletContext;

  /**
   * *.css
   * *.js
   * *.proto.httl
   */
  private final Set<String> presentFileNames = new HashSet<>();

  public static ComponentResourceManager instance() {
    return instance;
  }

  @PostConstruct
  public void init() {
    instance = this;

    File cssRoot = new File(servletContext.getRealPath("/css/"));
    Assert.isTrue(cssRoot.exists());
    for (File css : cssRoot.listFiles()) {
      if (css.getPath().endsWith(".css")) {
        presentFileNames.add(css.getName());
      }
    }

    File jsRoot = new File(servletContext.getRealPath("/js/"));
    Assert.isTrue(jsRoot.exists());
    for (File js : jsRoot.listFiles()) {
      if (js.getPath().endsWith(".js")) {
        presentFileNames.add(js.getName());
      }
    }
    
    File protoRoot = new File(servletContext.getRealPath("/WEB-INF/prototypes/"));
    Assert.isTrue(protoRoot.exists());
    for (File proto : protoRoot.listFiles()) {
      if (proto.getPath().endsWith(".proto.httl")) {
        presentFileNames.add(proto.getName());
      }
    }
  }

  public String includeCSS(String[] components) {
    StringBuilder sb = new StringBuilder();
    for (String each : components) {
      if (presentFileNames.contains(each + ".css")) {
        sb.append(includeOneCSS(each));
      }
    }
    return sb.toString();
  }

  public String includeJS(String[] components) {
    StringBuilder sb = new StringBuilder(includeOneJS("jquery-1.9.1")).append('\n');
    for (String each : components) {
      if (presentFileNames.contains(each + ".js")) {
        sb.append(includeOneJS(each));
      }
    }
    return sb.toString();
  }
  
  public Collection<String> includeProtos(String[] components) {
    Collection<String> protos = new ArrayList<>(components.length);
    for (String comp : components) {
      String pf = comp + ".proto.httl";
      if (presentFileNames.contains(pf)) {
        protos.add(pf);
      }
    }
    return protos;
  }

  private String includeOneCSS(String componentName) {
    return String.format("<link href=\"%s/css/%s.css\" rel=\"stylesheet\" "
        + "type=\"text/css\" media=\"screen\" />\n", StaticPathExposer.RS, componentName);
  }

  private String includeOneJS(String componentName) {
    return String.format("<script src=\"%s/js/%s.js\" "
        + "type=\"text/javascript\"></script>\n", StaticPathExposer.RS, componentName);
  }
}
