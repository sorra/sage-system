package sage.service

import com.avaje.ebean.Ebean
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import sage.domain.permission.FileItemPermission
import sage.entity.FileItem
import sage.util.Settings
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Service
class FileService {

  enum class Folder {
    PIC, AVATAR
  }

  private final val fileManagers: MutableMap<Folder, FileManager> = EnumMap(Folder::class.java)

  init {
    fileManagers[Folder.PIC] = FileManager(SUBDIR_PIC)
    fileManagers[Folder.AVATAR] = FileManager(SUBDIR_AVATAR)
  }

  fun picDir(): String = fileManagers[Folder.PIC]!!.dir.path

  fun avatarDir(): String = fileManagers[Folder.AVATAR]!!.dir.path

  @Throws(IOException::class)
  fun upload(userId: Long, file: MultipartFile, folder: Folder): String {
    val fm = fileManagers[folder]!!
    return saveFile(userId, fm, folder, file)
  }

  @Throws(IOException::class)
  fun multiUpload(userId: Long, files: Array<MultipartFile>, folder: Folder): Collection<String> {
    val fm = fileManagers[folder]!!
    return files.map { file -> saveFile(userId, fm, folder, file) }
  }

  fun delete(userId: Long, fileId: Long) {
    Ebean.execute {
      val fileItem = FileItem.get(fileId)
      FileItemPermission(userId, fileItem).canDelete()
      fileItem.delete()
    }
  }

  @Throws(IOException::class)
  private fun saveFile(userId: Long, fm: FileManager, folder: Folder, file: MultipartFile): String {
    if (!isPictureFileSizeAllowed(file.size)) {
      throw IllegalArgumentException("图片文件大小必须为0~${MAX_PICTURE_BYTES}MB!")
    }

    val bytes = file.bytes
    // 后缀必须限定为图片格式，以防脚本注入攻击
    val filename = "" + fm.nextNumber() + SUFFIX
    val webPath = folder.name.toLowerCase() + "/" + filename
    val storePath = Paths.get(fm.dir.path + "/" + filename)

    Files.write(storePath, bytes, StandardOpenOption.CREATE_NEW)
    FileItem(filename, webPath, storePath.toString(), userId).save()
    log.info("File saved: {}", storePath)

    return "/files/$webPath"
  }

  private fun isPictureFileSizeAllowed(size: Long): Boolean {
    return size in 1..MAX_PICTURE_BYTES
  }

  private class FileManager {
    val dir: File
    private val maxNumber = AtomicLong()

    internal fun nextNumber(): Long {
      return maxNumber.incrementAndGet()
    }

    constructor(subdir: String) {

      File(FILESTORE_HOME).let {
        if (!it.exists()) {
          log.warn("FILESTORE_HOME is missing, recreate $FILESTORE_HOME")
          it.mkdirs()
        }
      }

      dir = File(FILESTORE_HOME + subdir).let {
        if ((it.exists() || it.mkdirs()) && it.canWrite()) {
          log.info("Select the upload directory: {}", it)
          it
        } else {
          throw IllegalStateException("Cannot create the upload directory! user.home=" + System.getProperty("user.home"))
        }
      }

      // Find the largest existing number in files
      var max: Long = 0
      for (name in dir.list()!!) {
        if (name.startsWith(".") || name.startsWith("color")) {
          continue
        }
        try {
          val current = java.lang.Long.parseLong(name.substring(0, name.length - SUFFIX.length))
          if (current > max) {
            max = current
          }
        } catch (e: NumberFormatException) {
          log.warn("During init, non-number named file detected: {}", name)
        }
      }
      maxNumber.set(max)
    }
  }

  companion object {
    private const val MAX_PICTURE_MBS = 4L
    private const val MAX_PICTURE_BYTES = MAX_PICTURE_MBS * 1024 * 1024
    // Only for trial
    private const val SUFFIX = ".jpg"
    private const val SUBDIR_PIC = "/pic_files"
    private const val SUBDIR_AVATAR = "/avatar_files"

    private val FILESTORE_HOME = {
      val value = Settings.getProperty("filestore.home")
          ?: throw IllegalArgumentException("filestore.home is not set!")

      if (value.startsWith("~/")) {
        value.replaceFirst("~", System.getProperty("user.home"))
      } else {
        value
      }
    }()

    private val log = LoggerFactory.getLogger(FileService::class.java)
  }
}
