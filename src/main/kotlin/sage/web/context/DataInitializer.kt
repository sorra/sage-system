package sage.web.context

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.web.context.WebApplicationContext
import sage.service.BlogService
import sage.service.ServiceInitializer
import sage.service.TagService
import sage.service.TweetPostService
import java.io.*
import java.util.*

@Component
class DataInitializer {
  private val logger = LoggerFactory.getLogger(javaClass)
  @Autowired
  private val na: ServiceInitializer? = null
  @Autowired
  private val wac: WebApplicationContext? = null
  @Autowired
  private val tagService: TagService? = null
  @Autowired
  private val blogService: BlogService? = null
  @Autowired
  private val tweetPostService: TweetPostService? = null

  fun init() {
    val docRootPath = wac!!.servletContext.getRealPath("/docs")
    logger.info("Reading docRootPath: " + docRootPath)
    val docFolder = File(docRootPath)
    Assert.isTrue(docFolder.exists(), "$docRootPath not exist!")
    for (doc in docFolder.listFiles()!!) {
      try {
        BufferedReader(InputStreamReader(FileInputStream(doc), "UTF-8")).use { br -> loadDoc(br) }
      } catch (e: IOException) {
        logger.error("Fail at file: " + doc.absolutePath, e)
      }

    }
  }

  @Throws(IOException::class)
  private fun loadDoc(br: BufferedReader) {

    val tags = br.readLine()
    val title = br.readLine()
    if (tags == null || title == null) {
      logger.error("File: {}, please check its data!")
      return
    }
    val contentBuilder = StringBuilder()
    var line: String? = ""
    while (line != null) {
      line = br.readLine()
      if (line != null) {
        contentBuilder.append(line).append('\n')
      }
    }
    val content = contentBuilder.toString()

    val tagNames = tags.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val tagIds = tagNames.flatMap { tagName ->
      tagService!!.getTagsByName(tagName).map { it.id }
    }.toSet()
    val uid = Math.abs(Random().nextLong()) % 3 + 1
    logger.info("###{} ###author: {}", title, uid)
    blogService!!.post(uid, title, content, tagIds, "markdown")
//    tweetPostService!!.share(uid, blog)
  }
}
