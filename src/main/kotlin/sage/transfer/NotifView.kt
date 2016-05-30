package sage.transfer

import java.util.Date

import sage.entity.Notification

class NotifView {
  var id: Long = 0
  var ownerId: Long = 0
  var sender: UserLabel? = null
  var time: Date? = null
  var type: String  = ""
  var desc: String = ""
  var source: String = ""
  var read = false

  internal constructor() {
  }

  constructor(notification: Notification, sender: UserLabel, source: String) {
    id = notification.id
    ownerId = notification.ownerId!!
    this.sender = sender
    time = notification.whenCreated
    type = notification.type!!.name
    desc = notification.type!!.desc
    this.source = source
  }
}
