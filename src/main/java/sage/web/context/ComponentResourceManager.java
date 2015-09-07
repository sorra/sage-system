package sage.web.context;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
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

  private boolean developmentMode = true;

  public boolean isDevelopmentMode() {return developmentMode;}
  public void setDevelopmentMode(boolean value) {developmentMode = value;}

  /**
   * *.css
   * *.js
   * *.proto.httl
   */
  private final Set<String> presentFileNames = new HashSet<>();

  private final Map<String, JsTemplate> templates = new HashMap<>();

  public static ComponentResourceManager instance() {
    return instance;
  }

  @PostConstruct
  public void init() {
    instance = this;

    addFileType(".css", "/css/");
    addFileType(".js", "/js/");
    forFileType(".tmpl", "/WEB-INF/templates/", file -> templates.put(file.getName(), new JsTemplate(file)));
  }

  private void addFileType(String postfix, String root) {
    forFileType(postfix, root, file -> presentFileNames.add(file.getName()));
  }

  private void forFileType(String postfix, String root, Consumer<File> func) {
    File rootDir = new File(servletContext.getRealPath(root));
    Assert.isTrue(rootDir.exists());
    for (File file : rootDir.listFiles()) {
      if (file.getPath().endsWith(postfix)) {
        func.accept(file);
      }
    }
  }

  public String includeCSS(String[] components) {
    if (components == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (String each : components) {
      if (presentFileNames.contains(each + ".css")) {
        sb.append(includeOneCSS(each));
      }
    }
    return sb.toString();
  }

  public String includeJS(String[] components) {
    StringBuilder sb = new StringBuilder(160);
    sb.append(includeOneJS("jquery-1.9.1"));
    sb.append(includeOneJS("libs"));
    if (components == null) {
      return sb.toString();
    }
    sb.append(includeOneJS("template"));
    for (String each : components) {
      if (presentFileNames.contains(each + ".js")) {
        sb.append(includeOneJS(each));
      }
    }
    return sb.toString();
  }
  
  public Collection<String> includeProtos(String[] components) {
    if (components == null) {
      return new ArrayList<>();
    }
    Collection<String> protos = new ArrayList<>(components.length);
    protos.add("proto.httl");
    for (String comp : components) {
      String pf = comp + ".proto.httl";
      if (presentFileNames.contains(pf)) {
        protos.add(pf);
      }
    }
    return protos;
  }

  public Collection<String> includeTemplates(String[] components) {
    if (components == null) {
      return new ArrayList<>();
    }
    Collection<String> tmpls = new ArrayList<>(components.length);
    for (String comp : components) {
      JsTemplate tmpl = templates.get(comp+".tmpl");
      if (tmpl != null) {
        tmpls.add(tmpl.getSource());
      }
    }
    return tmpls;
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
