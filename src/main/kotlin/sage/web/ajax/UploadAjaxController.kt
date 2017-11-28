package sage.web.ajax

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import sage.domain.commons.DomainException
import sage.service.FilesService
import sage.web.auth.Auth
import sage.web.context.BaseController
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/upload")
class UploadAjaxController() : BaseController() {
  @PostMapping("/pic", produces = arrayOf("application/json"))
  @ResponseBody
  fun uploadPic(file: MultipartFile, response: HttpServletResponse): Map<String, String> {
    val uid = Auth.checkUid()
    log.info("uploadPic: uid={}, filename={}", uid, file.name)
    try {
      val link = filesService.upload(uid, file, FilesService.Folder.PIC)
      return mapOf("location" to link)
    } catch (e: IOException) {
      throw DomainException("uploadPic failed!", e)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(UploadAjaxController::class.java)
  }
}
