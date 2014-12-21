package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.ServiceInitializer;
import sage.web.context.DataInitializer;

@RestController
public class ZInitController {
  @Autowired
  private ServiceInitializer si;
  @Autowired
  private DataInitializer di;
  @RequestMapping("/z-init")
  public String initData() {
    si.init();
    di.init();
    return "Done.";
  }
}
