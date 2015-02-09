package sage.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sage.web.auth.Auth;

@RestController
@RequestMapping(value = "/upload", method = RequestMethod.POST)
public class UploadController {
  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  private static final long MAX_PICTURE_BYTES = 4*1024*1024;
  // Only for trial
  private static final String SUFFIX = ".jpg";
  private static final String[] DIR_ROOTS = {"C:", "D:", System.getProperty("user.home")};
  private static final String SUBDIR = "/programs/pics_upload";
  private String UPLOAD_DIR;

  private AtomicLong maxNumber = new AtomicLong();
  {
    File dir = null;
    for (String root : DIR_ROOTS) {
      if (new File(root).exists()) {
        UPLOAD_DIR = root + SUBDIR;
        dir = new File(UPLOAD_DIR);
        if ((dir.exists() || dir.mkdirs()) && dir.canWrite()) {
          log.info("Select the Upload Directory: " + UPLOAD_DIR);
          break;
        }
      }
    }
    if (dir == null) {
      throw new RuntimeException("Cannot create upload directory! user.home=" + System.getProperty("user.home"));
    }

    long max = 0;
    for (String name : dir.list()) {
      String numStr = name.substring(0, name.length() - SUFFIX.length());
      long current = Long.parseLong(numStr);
      if (current > max) {
        max = current;
      }
    }
    maxNumber.set(max);
  }
  
  @RequestMapping("/picture")
  public Collection<String> uploadPicture(@RequestParam MultipartFile[] files) throws IOException {
    Auth.checkCuid();
    
    Collection<String> destinations = new ArrayList<>();    
    for (MultipartFile file : files) {
      if (!isPictureFileSizeAllowed(file.getSize())) {
        throw new RuntimeException("图片文件太大了!");
      }
      byte[] bytes = file.getBytes();
      // 后缀必须限定为图片格式，以防脚本注入攻击
      String name = maxNumber.incrementAndGet() + SUFFIX;
      Path path = Paths.get(UPLOAD_DIR + "/" + name);

      Files.write(path, bytes, StandardOpenOption.CREATE_NEW);
      destinations.add(name);

    /*
      try (InputStream in = file.getInputStream()) {
        long written = Files.copy(in, path);
        if (written == file.getSize()) {
          destinations.add(number + ".jpg");
        } else {
          Files.delete(path);
        }
      }
    */
    }
    
    return destinations;
  }
  
  private boolean isPictureFileSizeAllowed(long size) {
    return size > 0 && size <= MAX_PICTURE_BYTES;
  }
}
