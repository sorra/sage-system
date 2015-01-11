package sage.web.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

@SuppressWarnings("serial")
public class FrontMap extends ModelMap {
  public static final String NAME = "frontMap";
  
  protected FrontMap() {}

  /**
   *  Get the front map from model, create one if not exist
   * @param model
   * @return the front map
   */
  public static FrontMap from(ModelMap model) {
    FrontMap fm = (FrontMap) model.get(NAME);
    if (fm == null) {
      fm = new FrontMap();
      model.addAttribute(NAME, fm);
    }
    return fm;
  }

  public FrontMap attr(String key, Object value) {
    addAttribute(key, value);
    return this;
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
