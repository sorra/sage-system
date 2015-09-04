package sage.web.page;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger log = LoggerFactory.getLogger(UserPageController.class);
  @Autowired
  private UserService userService;
  @Autowired
  private FilesService filesService;

  @RequestMapping(value = "/user-info", method = RequestMethod.GET)
  String info(@RequestParam(required = false) String next, ModelMap model) {
    Long cuid = Auth.checkCuid();
    model.put("user", userService.getUserLabel(cuid));
    String action = "user-info";
    if (StringUtils.isNotBlank(next)) {
      action += ("?next="+next);
    }
    model.put("action", action);
    return "user-info";
  }

  @RequestMapping(value = "/user-info", method = RequestMethod.POST)
  String changeInfo(@RequestParam(required = false) String name,
                    @RequestParam(required = false) String intro,
                    @RequestParam(required = false) MultipartFile avatar,
                    @RequestParam(required = false) String next) throws IOException {
    Long cuid = Auth.checkCuid();
    String path;
    if (avatar == null || avatar.isEmpty()) {
      path = null;
    } else {
      path = filesService.upload(cuid, avatar, FilesService.Folder.AVATAR);
    }
    userService.changeInfo(cuid, name, intro, path);
    if (StringUtils.isNotBlank(next)) {
      return "redirect:" + Auth.decodeLink(next);
    }
    return "redirect:/user-info";
  }

  @RequestMapping(value = "/change-password", method = RequestMethod.GET)
  String pageChangePassword() {
    Auth.checkCuid();
    return "change-password";
  }

  @RequestMapping(value = "/change-password", method = RequestMethod.POST)
  String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, ModelMap model) {
    Long cuid = Auth.checkCuid();
    try {
      boolean match = userService.updatePassword(cuid, oldPassword, newPassword);
      if (match) {
        model.put("success", true);
        model.put("serverMsg", "修改成功");
      } else {
        model.put("serverMsg", "旧密码输入不对");
      }
    } catch (Exception e) {
      log.error("changePassword", e);
      model.put("serverMsg", "未知错误");
    }
    return "change-password";
  }
}
