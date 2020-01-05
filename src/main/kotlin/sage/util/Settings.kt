package sage.util

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*


/**
 * Custom settings to override default values.
 *
 * Precedence:
 * 1. JVM system property
 * 2. The settings file in user home directory
 * 3. The settings file in classpath
 */
object Settings {

  private const val FILE_NAME = "sage-settings.properties"

  private val charset = StandardCharsets.UTF_8

  private val propsFromFile: Properties = {
    val properties = Properties()

    // Load default file
    Thread.currentThread().contextClassLoader.getResourceAsStream(FILE_NAME)?.let {
      properties.load(InputStreamReader(it, charset))
    }

    // Load user file
    val userFile = File(System.getProperty("user.home") + FILE_NAME)
    if (userFile.exists()) {
      properties.load(InputStreamReader(FileInputStream(userFile), charset))
    }

    properties
  }()

  fun getProperty(key: String): String? {
    return System.getProperty(key) ?: propsFromFile.getProperty(key)
  }
}
