package sage.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import sage.web.auth.AuthUtil;

@Controller
@RequestMapping(value = "/upload", method = RequestMethod.POST)
public class UploadController {
  private static final long MAX_IMAGE_BYTES = 4*1024*1024;
  
  // Only for trial
  private static final String UPLOAD_DIR = "D:/programs/upload";
  // Only for trial
  private AtomicLong maxNumber = new AtomicLong();
  {
    long max = 0;
    File dir = new File(UPLOAD_DIR);
    if (!dir.exists() && !dir.mkdirs()) {
      throw new RuntimeException("Cannot mkdir of " + UPLOAD_DIR);
    }
    for (String name : dir.list()) {
      String numStr = name.substring(0, name.length() - ".jpg".length());
      long current = Long.parseLong(numStr);
      if (current > max) {
        max = current;
      }
    }
    maxNumber.set(max);
  }
  
  @RequestMapping("/image")
  @ResponseBody
  public Collection<String> uploadImage(@RequestParam("files") MultipartFile[] files) throws IOException {
    AuthUtil.checkCurrentUid();
    
    Collection<String> destinations = new ArrayList<>();    
    for (MultipartFile file : files) {
      if (isImageFileSizeAllowed(file.getSize())) {
        long number = maxNumber.incrementAndGet(); // Permanent increment, even if upload fails
        Path path = Paths.get(UPLOAD_DIR + "/" + number + ".jpg");
        try (InputStream in = file.getInputStream()) {
          long written = Files.copy(in, path);
          if (written == file.getSize()) {
            destinations.add(number + ".jpg");
          } else {
            Files.delete(path);
          }
        }
      }
    }
    
    return destinations;
  }
  
  private boolean isImageFileSizeAllowed(long size) {
    if (size > 0 && size <= MAX_IMAGE_BYTES) {
      return true;
    }
    return false;
  }
}
