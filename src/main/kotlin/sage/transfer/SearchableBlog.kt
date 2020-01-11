package sage.transfer

import sage.annotation.KotlinNoArg
import java.sql.Timestamp

@KotlinNoArg
class SearchableBlog(
    var id: Long,
    var author: UserLabel,

    var title: String,
    var content: String,
    var whenCreated: Timestamp?,
    var whenEdited: Timestamp?,
    tags: Collection<TagLabel> = mutableListOf()
) : Searchable {
  var tags: Collection<TagLabel> = HashSet(tags)
}
