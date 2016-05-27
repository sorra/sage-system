package sage.transfer

import java.sql.Timestamp

fun actualWhenModified(whenCreated: Timestamp?, whenModified: Timestamp?): Timestamp? {
  if (whenCreated == null || whenModified == null) {
    return null
  } else if (whenModified > whenCreated) {
    return whenModified
  }
  return null
}