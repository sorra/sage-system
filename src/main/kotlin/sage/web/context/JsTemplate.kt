package sage.web.context

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

open class JsTemplate(private val file: File) {

  var source: String = ""
    get() {
      if (file.lastModified() != loadedModifiedTime) {
        load()
      }
      return field
    }
    private set

  private var loadedModifiedTime: Long = 0

  init {
    load()
  }

  private fun load() {
    source = Files.readAllBytes(file.toPath()).toString(StandardCharsets.UTF_8)
    loadedModifiedTime = file.lastModified()
  }
}
