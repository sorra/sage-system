package sage.web.page;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sage.domain.service.FilesService;
import sage.domain.service.UserService;
import sage.web.auth.Auth;

@Controller
public class UserPageController {
  @Autowired
  private UserService userService;
  @Autowired
  private FilesService filesService;

  @RequestMapping(value = "/user-info", method = RequestMethod.GET)
  String info(ModelMap model) {
    Long cuid = Auth.checkCuid();
    model.put("user", userService.getUserLabel(cuid));
    return "user-info";
  }

  @RequestMapping(value = "/user-info", method = RequestMethod.POST)
  String changeInfo(@RequestParam String intro, @RequestParam(required = false) MultipartFile avatar) throws IOException {
    Long cuid = Auth.checkCuid();
    String path;
    if (avatar != null) {
      path = "/files/" + filesService.upload(avatar, FilesService.Folder.AVATAR);
    } else {
      path = null;
    }
    userService.changeInfo(cuid, intro, path);
    return "redirect:/user-info";
  }
}
