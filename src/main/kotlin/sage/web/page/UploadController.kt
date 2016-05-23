package sage.web.page

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import sage.domain.commons.DomainException
import sage.service.FilesService
import sage.web.auth.Auth
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping(method = arrayOf(RequestMethod.POST))
open class UploadController {
  @Autowired
  private val filesService: FilesService? = null

  @RequestMapping("/pic-upload")
  open fun picUpload(picFile: MultipartFile, response: HttpServletResponse) {
    val cuid = Auth.checkUid()
    try {
      log.info("Uploading pic, uid={}", cuid)
      val link = filesService!!.upload(cuid, picFile, FilesService.Folder.PIC)
      response.contentType = "text/plain"
      response.writer.write(link)
    } catch (e: IOException) {
      throw DomainException("文件上传失败", e)
    }

  }

  companion object {
    private val log = LoggerFactory.getLogger(UploadController::class.java)
  }
}
