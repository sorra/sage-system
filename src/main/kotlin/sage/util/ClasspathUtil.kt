package sage.util

object ClasspathUtil {
  /**
   * The default ClassLoader
   */
  fun classLoader(): ClassLoader = Thread.currentThread().contextClassLoader
}