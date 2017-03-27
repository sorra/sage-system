package sage.service

import com.avaje.ebean.Ebean
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import sage.domain.commons.DomainException
import sage.domain.permission.CheckPermission
import sage.entity.FileItem
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Service
class FilesService {

  enum class Folder {
    PIC, AVATAR
  }

  private val fileManagers = HashMap<Folder, FileManager>()

  init {
    fileManagers.put(Folder.PIC, FileManager(SUBDIR_PIC))
    fileManagers.put(Folder.AVATAR, FileManager(SUBDIR_AVATAR))
  }

  fun picDir(): String = fileManagers[Folder.PIC]!!.DIR!!

  fun avatarDir(): String = fileManagers[Folder.AVATAR]!!.DIR!!

  @Throws(IOException::class)
  fun upload(userId: Long, file: MultipartFile, folder: FilesService.Folder): String {
    val fm = fileManagers[folder]!!
    return saveFile(userId, fm, folder, file)
  }

  @Throws(IOException::class)
  fun multiUpload(userId: Long, files: Array<MultipartFile>, folder: FilesService.Folder): Collection<String> {
    val fm = fileManagers[folder]!!
    return files.map { file -> saveFile(userId, fm, folder, file) }
  }

  fun delete(userId: Long, fileId: Long) {
    Ebean.execute({
      val fileItem = FileItem.byId(fileId) ?: throw DomainException("FileItem[%s] does not exist", fileId)
      CheckPermission.canDelete(userId, fileItem, userId == fileItem.ownerId)

      //TODO Soft delete file content?
      fileItem.delete()
    })
  }

  @Throws(IOException::class)
  private fun saveFile(userId: Long, fm: FileManager, folder: Folder, file: MultipartFile): String {
    if (!isPictureFileSizeAllowed(file.size)) {
      throw IllegalArgumentException("图片文件太大了!")
    }
    val bytes = file.bytes
    // 后缀必须限定为图片格式，以防脚本注入攻击
    val filename = "" + fm.nextNumber() + SUFFIX
    val webPath = folder.name.toLowerCase() + "/" + filename
    val storePath = Paths.get(fm.DIR + "/" + filename)
    Files.write(storePath, bytes, StandardOpenOption.CREATE_NEW)
    FileItem(filename, webPath, storePath.toString(), userId).save()
    log.info("File saved: " + storePath)
    return "/files/" + webPath
  }

  private fun isPictureFileSizeAllowed(size: Long): Boolean {
    return size > 0 && size <= MAX_PICTURE_BYTES
  }

  private class FileManager internal constructor(subdir: String) {
    var DIR: String? = null
    private val maxNumber = AtomicLong()

    internal fun nextNumber(): Long {
      return maxNumber.incrementAndGet()
    }

    init {
      var dir: File? = null
      val root = ENV_SAGE_FILES_HOME ?: USER_HOME
      if (File(root).exists()) {
        DIR = root + subdir
        dir = File(DIR)
        if ((dir.exists() || dir.mkdirs()) && dir.canWrite()) {
          log.info("Select the Upload Directory: " + DIR)
        } else {
          dir = null
        }
      }
      if (dir == null) {
        throw RuntimeException("Cannot create upload directory! user.home=" + System.getProperty("user.home"))
      }
      // Find the largest existing number in files
      var max: Long = 0
      for (name in dir.list()) {
        if (name.startsWith(".") || name.startsWith("color")) {
          continue
        }
        try {
          val current = java.lang.Long.parseLong(name.substring(0, name.length - SUFFIX.length))
          if (current > max) {
            max = current
          }
        } catch (e: NumberFormatException) {
          log.warn("Non-number named file detected: " + name)
        }
      }
      maxNumber.set(max)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(FilesService::class.java)
    private val MAX_PICTURE_BYTES = 4 * 1024 * 1024.toLong()
    // Only for trial
    private val SUFFIX = ".jpg"
    private val ENV_SAGE_FILES_HOME = System.getenv("SAGE_FILES_HOME")
    private val USER_HOME = System.getProperty("user.home")
    private val SUBDIR_PIC = "/programs/pic_files"
    private val SUBDIR_AVATAR = "/programs/avatar_files"
  }
}
