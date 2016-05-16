package sage.web.page;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import sage.domain.commons.DomainException;
import sage.service.FilesService;
import sage.web.auth.Auth;

@Controller
@RequestMapping
public class UploadController {
  private static final Logger log = LoggerFactory.getLogger(UploadController.class);
  @Autowired
  private FilesService filesService;

  @RequestMapping("/pic-upload")
  public void picUpload(MultipartFile picFile, HttpServletResponse response) {
    Long cuid = Auth.checkCuid();
    try {
      log.info("Uploading pic, uid={}", cuid);
      String link = filesService.upload(cuid, picFile, FilesService.Folder.PIC);
      response.setContentType("text/plain");
      response.getWriter().write(link);
    } catch (IOException e) {
      throw new DomainException("文件上传失败", e);
    }
  }
}
