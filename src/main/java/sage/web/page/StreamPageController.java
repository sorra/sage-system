package sage.web.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.entity.Tag;
import sage.transfer.TagCard;
import sage.transfer.TagLabel;
import sage.web.auth.AuthUtil;
import sage.web.context.FrontMap;

@Controller
public class StreamPageController {
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;

  @RequestMapping("/public/{id}")
  public String publicPage(@PathVariable("id") long id, ModelMap model) {
    FrontMap fm = FrontMap.from(model);
    fm.put("id", id);
    
    List<TagLabel> coreTags = new ArrayList<>();
    List<TagLabel> nonCoreTags = new ArrayList<>();
    TagCard tagCard = tagService.getTagCard(id);
    for (TagLabel child : tagCard.getChildren()) {
      if (child.getIsCore()) {
        coreTags.add(child);
      } else {
        nonCoreTags.add(child);
      }
    }
    
    Collection<Tag> sameNameTags = tagService.getSameNameTags(id);
    
    fm.put("coreTags", coreTags);
    fm.put("nonCoreTags", nonCoreTags);
    fm.put("sameNameTags", sameNameTags);
    fm.put("relatedTags", null);
    
    return "public-page";
  }

  @RequestMapping("/private")
  public String privatePage() {
    return "forward:/private/" + AuthUtil.checkCurrentUid();
  }

  @RequestMapping("/private/{id}")
  public String privatePage(@PathVariable("id") long id, ModelMap model) {
    Long uid = AuthUtil.checkCurrentUid();
    FrontMap fm = FrontMap.from(model);
    
    fm.put("id", id);
    if (uid.equals(id)) {
      fm.put("isSelfPage", true);
    }
    fm.put("thisUser", userService.getUserCard(uid, id));
    
    model.remove("userSelfJson");
    return "private-page";
  }

  @RequestMapping("/group/{id}")
  public String groupPage(@PathVariable("id") long id) {
    return "group-page";
  }
}
