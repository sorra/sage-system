package sage.transfer

import java.sql.Timestamp

fun actualWhenEdited(whenCreated: Timestamp?, whenEdited: Timestamp?): Timestamp? {
  if (whenCreated == null || whenEdited == null) {
    return null
  } else if (whenEdited > whenCreated) {
    return whenEdited
  }
  return null
}