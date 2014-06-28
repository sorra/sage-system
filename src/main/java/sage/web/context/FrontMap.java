package sage.web.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

@SuppressWarnings("serial")
public class FrontMap extends ModelMap {
  public static final String NAME = "frontMap";
  
  protected FrontMap() {}
  
  public static FrontMap from(ModelMap model) {
    FrontMap fm = new FrontMap();
    model.addAttribute(NAME, fm);
    return fm;
  }

  /**
   * Use for rendering in template engine
   * @return JSON string
   */
  @Override
  public String toString() {
    logger.debug("Keys: " + this.keySet());
    return Json.json(this);
  }

  private static final Logger logger = LoggerFactory.getLogger(FrontMap.class);
}
