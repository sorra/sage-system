package sage.util

import java.io.File
import java.io.FileInputStream
import java.util.*


/**
 * Custom settings to override default values.
 *
 * Precedence:
 * 1. JVM system property
 * 2. The settings file in user home directory
 */
object Settings {
  private val propsFromFile: Properties = {
    val path = System.getProperty("user.home") + "/sage-settings.properties"
    val properties = Properties()
    if (File(path).exists()) {
      properties.load(FileInputStream(path))
    }
    properties
  }()

  fun getProperty(key: String): String? {
    return System.getProperty(key) ?: propsFromFile.getProperty(key)
  }
}
