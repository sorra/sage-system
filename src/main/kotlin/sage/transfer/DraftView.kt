package sage.transfer

import sage.entity.Draft
import java.sql.Timestamp


class DraftView {
  var id: Long = 0L
  var title: String = ""
  var content: String = ""
  var whenCreated: Timestamp? = null
  var whenModified: Timestamp? = null

  internal constructor() {}

  constructor(draft: Draft) {
    this.id = draft.id
    this.title = draft.title
    this.content = draft.content
    this.whenCreated = draft.whenCreated
    this.whenModified = draft.whenModified
  }
}