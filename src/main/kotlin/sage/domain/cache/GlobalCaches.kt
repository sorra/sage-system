package sage.domain.cache

import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit

object GlobalCaches {
  val blogsCache = createCache()
  val topicsCache = createCache()
  val tweetsCache = createCache()

  private fun createCache() = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(5, TimeUnit.MINUTES).build<String, Any?>()
}