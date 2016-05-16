package sage.service

import org.springframework.stereotype.Service
import sage.entity.Blog
import sage.transfer.BlogView

@Service
class BlogReadService {

  fun byId(blogId: Long) = Blog.byId(blogId)?.run { BlogView(this) }

  fun all() = Blog.all().map { BlogView(it) }
}
