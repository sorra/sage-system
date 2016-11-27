package sage.domain.cache

import com.avaje.ebean.Model
import com.google.common.cache.CacheBuilder
import sage.entity.*
import sage.util.PaginationLogic
import java.util.concurrent.TimeUnit

object GlobalCaches {
  val blogsCache = ListCache(Blog)
  val topicsCache = ListCache(TopicPost)
  val tweetsCache = ListCache(Tweet)

  class ListCache<V : BaseModel>(val find: Model.Find<Long, V>) {
    private val cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(5, TimeUnit.MINUTES).build<String, Pair<List<Long>, Int>>()

    operator fun get(key: String, valueLoader: ()->List<V>): List<V> {
      return cache.getIfPresent(key)?.run {
        first.mapNotNull { find.byId(it) }
      } ?: run {
        val list = valueLoader()
        cache.put(key, list.map { it.id } to 0)
        list
      }
    }

    operator fun get(name: String, page: Int, size: Int): Pair<List<V>, Int> {
      val key = "$name?page=$page&size=$size"
      return cache.getIfPresent(key)?.run {
        val (ids, pagesCount) = this
        find.where().`in`("id", ids).findList() to pagesCount
      } ?: run {
        val entities = find.orderBy("id desc").findPagedList(page - 1, size).list
        val pagesCount = PaginationLogic.pagesCount(size, getRecordsCount(Blog))
        cache.put(key, entities.map { it.id } to pagesCount)
        entities to pagesCount
      }
    }

    fun clear() {
      cache.invalidateAll()
    }
  }
}