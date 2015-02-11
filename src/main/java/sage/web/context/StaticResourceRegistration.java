package sage.web.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sage.domain.service.FilesService;

@Configuration
@EnableWebMvc
public class StaticResourceRegistration extends WebMvcConfigurerAdapter {
  @Autowired
  private FilesService filesService;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/files/pic/**").addResourceLocations(toLocation(filesService.picDir()));
    registry.addResourceHandler("/files/avatar/**").addResourceLocations(toLocation(filesService.avatarDir()));
  }

  private String toLocation(String path) {
    if (path.startsWith("/")) {
      return "file:" + path + "/";
    } else{
      return "file:///" + path + "/";
    }
  }
}
