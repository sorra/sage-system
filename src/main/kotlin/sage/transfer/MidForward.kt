package sage.transfer

import sage.annotation.KotlinNoArg

@KotlinNoArg
data class MidForward(var id: Long = 0, var authorId: Long = 0, var authorName: String = "", var content: String = "")
