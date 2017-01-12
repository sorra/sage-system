package sage.transfer

import sage.annotation.KotlinNoArg

@KotlinNoArg
class TagChangeRequestCard {
  var id: Long = 0
  var tag: TagLabel? = null
  var submitter: UserLabel? = null
  var transactor: UserLabel? = null
  var statusKey: String = ""
  var status: String = ""
  var type: String = ""
  var desc: String = ""
}
