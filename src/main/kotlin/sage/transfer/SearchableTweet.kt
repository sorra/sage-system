package sage.transfer

import sage.annotation.KotlinNoArg
import java.sql.Timestamp

@KotlinNoArg
class SearchableTweet(
    var id: Long,
    var author: UserLabel,

    var content: String,

    var whenCreated: Timestamp?,

    var tags: Collection<TagLabel> = mutableListOf()
) : Searchable
