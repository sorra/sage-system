package sage.web.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
import sage.web.auth.Auth;
import sage.web.context.FrontMap;

@Controller
public class StreamPageController {
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;

  @RequestMapping("/public/{id}")
  public String publicPage(@PathVariable long id, ModelMap model) {
    FrontMap fm = FrontMap.from(model);
    fm.put("id", id);
    
    List<TagLabel> coreTags = new ArrayList<>();
    List<TagLabel> nonCoreTags = new ArrayList<>();
    Optional<Tag> oTag = tagService.getTag(id);
    if (oTag.isPresent()) {
      model.put("intro", oTag.get().getIntro());
      for (TagLabel child : new TagCard(oTag.get()).getChildren()) {
        if (child.getIsCore()) {
          coreTags.add(child);
        } else {
          nonCoreTags.add(child);
        }
      }
    } else {
      throw new IllegalArgumentException("tag id " + id + " does not exist!");
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
    return "forward:/private/" + Auth.checkCurrentUid();
  }

  @RequestMapping("/private/{id}")
  public String privatePage(@PathVariable long id, ModelMap model) {
    Long uid = Auth.checkCurrentUid();
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
  public String groupPage(@PathVariable long id) {
    return "group-page";
  }
}
