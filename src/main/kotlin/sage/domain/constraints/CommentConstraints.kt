package sage.domain.constraints

import sage.domain.commons.BadArgumentException

object CommentConstraints {
  private val COMMENT_MAX_LEN = 1000

  fun check(inputContent: String) {
    if (inputContent.isEmpty()) {
      throw BadArgumentException("Comment is blank!")
    }

    if (inputContent.length > COMMENT_MAX_LEN) {
      throw BadArgumentException("Comment is too long! Limit: ${COMMENT_MAX_LEN}. Actual: ${inputContent.length}")
    }
  }
}