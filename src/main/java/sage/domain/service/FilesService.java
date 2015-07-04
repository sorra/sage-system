package sage.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.repository.FileItemRepository;
import sage.entity.FileItem;

@Service
public class FilesService {
  private static final Logger log = LoggerFactory.getLogger(FilesService.class);
  private static final long MAX_PICTURE_BYTES = 4*1024*1024;
  // Only for trial
  private static final String SUFFIX = ".jpg";
  private static final String ENV_SAGE_FILES_HOME = System.getenv("SAGE_FILES_HOME");
  private static final String USER_HOME = System.getProperty("user.home");
  private static final String SUBDIR_PIC = "/programs/pic_files";
  private static final String SUBDIR_AVATAR = "/programs/avatar_files";

  public enum Folder {
    PIC, AVATAR
  }

  private final Map<Folder, FileManager> fileManagers = new HashMap<>(); {
    fileManagers.put(Folder.PIC, new FileManager(SUBDIR_PIC));
    fileManagers.put(Folder.AVATAR, new FileManager(SUBDIR_AVATAR));
  }

  public String picDir() {
    return fileManagers.get(Folder.PIC).DIR;
  }
  public String avatarDir() {
    return fileManagers.get(Folder.AVATAR).DIR;
  }

  @Autowired
  private FileItemRepository fileRepo;

  public String upload(long userId, MultipartFile file, FilesService.Folder folder) throws IOException {
    FileManager fm = fileManagers.get(folder);
    Objects.requireNonNull(fm);
    return saveFile(userId, fm, folder, file);
  }

  public Collection<String> multiUpload(long userId, MultipartFile[] files, FilesService.Folder folder) throws IOException {
    FileManager fm = fileManagers.get(folder);
    Objects.requireNonNull(fm);

    Collection<String> destinations = new ArrayList<>();
    for (MultipartFile file : files) {
      String name = saveFile(userId, fm, folder, file);
      destinations.add(name);
    }
    return destinations;
  }

  public void delete(long userId, long fileId) {
    FileItem fileItem = fileRepo.nonNull(fileId);
    if (userId != fileItem.getOwnerId()) {
      throw new DomainRuntimeException("User[%s] is not the owner of FileItem[%s]", userId, fileId);
    }
    // Soft delete file content?
    fileRepo.delete(fileItem);
  }

  private String saveFile(long userId, FileManager fm, Folder folder, MultipartFile file) throws IOException {
    if (!isPictureFileSizeAllowed(file.getSize())) {
      throw new IllegalArgumentException("图片文件太大了!");
    }
    byte[] bytes = file.getBytes();
    // 后缀必须限定为图片格式，以防脚本注入攻击
    String filename = fm.nextNumber() + SUFFIX;
    Path storePath = Paths.get(fm.DIR + "/" + filename);
    Files.write(storePath, bytes, StandardOpenOption.CREATE_NEW);

    String webPath = folder.name().toLowerCase() + "/" + filename;

    fileRepo.save(new FileItem(filename, webPath, storePath.toString(), userId));

    log.info("File saved: " + storePath);
    return webPath;
  }

  private boolean isPictureFileSizeAllowed(long size) {
    return size > 0 && size <= MAX_PICTURE_BYTES;
  }

  private static class FileManager {
    private String DIR;
    private AtomicLong maxNumber = new AtomicLong();

    long nextNumber() {return maxNumber.incrementAndGet();}

    FileManager(String subdir) {
      File dir = null;
      String root = ENV_SAGE_FILES_HOME != null ? ENV_SAGE_FILES_HOME : USER_HOME;
      if (new File(root).exists()) {
        DIR = root + subdir;
        dir = new File(DIR);
        if ((dir.exists() || dir.mkdirs()) && dir.canWrite()) {
          log.info("Select the Upload Directory: " + DIR);
        }
      }
      if (dir == null) {
        throw new RuntimeException("Cannot create upload directory! user.home=" + System.getProperty("user.home"));
      }
      // Find the largest existing number in files
      long max = 0;
      for (String name : dir.list()) {
        try {
          long current = Long.parseLong(name.substring(0, name.length() - SUFFIX.length()));
          if (current > max) {
            max = current;
          }
        } catch (NumberFormatException e) {
          log.warn("Non-number named file detected: " + name);
        }
      }
      maxNumber.set(max);
    }
  }
}
