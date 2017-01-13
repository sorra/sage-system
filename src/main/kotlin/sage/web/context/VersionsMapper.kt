package sage.web.context

import org.slf4j.LoggerFactory
import sage.util.Utils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.ServletContext

object VersionsMapper {
  private val log = LoggerFactory.getLogger(VersionsMapper.javaClass)
  private lateinit var dir: Path
  private val map: MutableMap<String, VersionedFile> = ConcurrentHashMap()

  fun setup(servletContext: ServletContext): VersionsMapper {
    dir = Paths.get(servletContext.getRealPath("/static/"))
    return this
  }

  fun getPath(uri: String): String {
    val version = getVersion(uri)
    if (version.isEmpty()) {
      return uri
    } else {
      val idx = uri.lastIndexOf(".")
      if (idx > 0) return "${uri.substring(0, idx)}_v_${version}${uri.substring(idx)}"
      else return uri
    }
  }

  private fun getVersion(path: String): String {
    if (!Utils.isStaticResource(path)) {
      log.error("Path $path should be CSS or JS and under /static/ !")
      return ""
    }

    var versionedFile = map[path] ?: try {
        log.info("First time to putVersionedFile path=$path")
        putVersionedFile(path, dir.resolve(path.drop("/static/".length)))
      } catch (e: Exception) {
        log.error("Error hashing the file: $path", e)
        return ""
      }

    val currentTime = System.currentTimeMillis()
    if (currentTime > versionedFile.lastChecked + 30000) { // 30s after last checked
      if (versionedFile.file.lastModified() > versionedFile.lastChecked) { // file is modified
        versionedFile = putVersionedFile(path, versionedFile.file.toPath(), currentTime)
      } else {
        versionedFile.lastChecked = currentTime;
      }
    }
    return versionedFile.version
  }

  private fun putVersionedFile(putPath: String, path: Path, time: Long = System.currentTimeMillis()): VersionedFile {
    val start = System.currentTimeMillis()
    val hasher = MessageDigest.getInstance("MD5")
    val version = Utils.toHexString(hasher.digest(Files.readAllBytes(path)))
    val versionedFile = VersionedFile(path.toFile(), version, time)
    val cost = System.currentTimeMillis() - start
    log.info("putVersionedFile ${cost}ms path=$putPath , version=$version")
    map.put(putPath, versionedFile)
    return versionedFile
  }

  class VersionedFile(val file: File, val version: String, @Volatile var lastChecked: Long)
}
