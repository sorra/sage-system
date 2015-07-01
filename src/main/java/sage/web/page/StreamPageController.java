package sage.web.page;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.domain.commons.BadArgumentException;
import sage.domain.service.GroupService;
import sage.domain.service.TagChangeService;
import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.transfer.TagCard;
import sage.transfer.TagLabel;
import sage.util.Colls;
import sage.web.auth.Auth;
import sage.web.context.FrontMap;

@Controller
public class StreamPageController {
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;
  @Autowired
  private TagChangeService tagChangeService;
  @Autowired
  private GroupService groupService;

  @RequestMapping("/public/{id}")
  public String publicPage(@PathVariable long id, ModelMap model) {
    FrontMap fm = FrontMap.from(model);
    fm.put("id", id);
    
    List<TagLabel> coreTags = new ArrayList<>();
    List<TagLabel> nonCoreTags = new ArrayList<>();
    Optional<TagCard> tagOpt = tagService.optTagCard(id);
    if (tagOpt.isPresent()) {
      TagCard tagCard = tagOpt.get();
      model.put("tag", tagCard);
      for (TagLabel child : tagCard.getChildren()) {
        if (child.getIsCore()) {
          coreTags.add(child);
        } else {
          nonCoreTags.add(child);
        }
      }
    } else {
      throw new BadArgumentException("tag id " + id + " does not exist!");
    }

    model.put("groups", groupService.byTags(Collections.singletonList(id)));

    model.put("countPendingRequestsOfTagScope", tagChangeService.countPendingRequestsOfTagScope(id));
    model.put("countPendingRequestsOfTag", tagChangeService.countPendingRequestsOfTag(id));

    Collection<TagLabel> sameNameTags = Colls.map(tagService.getSameNameTags(id), TagLabel::new);

    model.put("coreTags", coreTags);
    model.put("nonCoreTags", nonCoreTags);
    model.put("sameNameTags", sameNameTags);
    model.put("relatedTags", null);
    
    return "public-page";
  }

  @RequestMapping("/private")
  public String privatePage() {
    return "forward:/private/" + Auth.checkCuid();
  }

  @RequestMapping("/private/{id}")
  public String privatePage(@PathVariable long id, ModelMap model) {
    Long uid = Auth.checkCuid();
    FrontMap fm = FrontMap.from(model);
    
    fm.put("id", id);
    if (uid.equals(id)) {
      fm.put("isSelfPage", true);
    }
    fm.put("thisUser", userService.getUserCard(uid, id));
    
    return "private-page";
  }
}
