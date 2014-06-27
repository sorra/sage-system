package sage.web.context;

import java.util.Collection;

import httl.Context;

public class ViewComponentResource {
  public static String includeCSS() {
    String[] components = (String[]) Context.getContext().get("components");
    return ComponentResourceManager.instance().includeCSS(components);
  }

  public static String includeJS() {
    String[] components = (String[]) Context.getContext().get("components");
    return ComponentResourceManager.instance().includeJS(components);
  }
  
  public static Collection<String> includeProtos() {
    String[] components = (String[]) Context.getContext().get("components");
    return ComponentResourceManager.instance().includeProtos(components);
  }
}
