package sage.domain.constraints

import sage.domain.commons.BadArgumentException
import sage.entity.Blog


object BlogConstraints {
  private val BLOG_TITLE_MAX_LEN = 100
  private val BLOG_CONTENT_MAX_LEN = 10000

  fun check(blog: Blog) {
    if (blog.title.isBlank()) {
      throw BadArgumentException("Blog title is blank!")
    }

    if (blog.title.length > BLOG_TITLE_MAX_LEN) {
      throw BadArgumentException("Blog title is too long! Limit: ${BLOG_TITLE_MAX_LEN}. Actual: ${blog.title.length}")
    }

    if (blog.inputContent.isBlank()) {
      throw BadArgumentException("Blog input content is blank!")
    }

    if (blog.inputContent.length > BLOG_CONTENT_MAX_LEN) {
      throw BadArgumentException("Blog input content is too long! Limit: ${BLOG_CONTENT_MAX_LEN}. Actual: ${blog.inputContent.length}")
    }
  }
}