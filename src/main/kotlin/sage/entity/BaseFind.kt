package sage.entity

import com.avaje.ebean.Model
import sage.domain.commons.DomainException
import sage.util.PaginationLogic
import kotlin.reflect.KClass

abstract class BaseFind<K, T : Model> (private val entityClass: KClass<T>) : Model.Find<K, T>() {

  fun get(id: K) = getNonNull(entityClass, id)

  private fun <T : Any> getNonNull(beanType: KClass<T>, id: K): T = Model.db().find(beanType.java, id)
      ?: throw DomainException("${beanType.java.simpleName}[$id] does not exist")

  fun totalCount(): Int = where().findRowCount()

  fun findPageAndCountPages(pageIndex: Int, pageSize: Int): Pair<List<T>, Int> {
    val list = findPage(pageIndex, pageSize)
    val pagesCount = PaginationLogic.pagesCount(pageSize, totalCount())
    return list to pagesCount
  }

  fun findPage(pageIndex: Int, pageSize: Int): List<T> =
      where().orderBy("id desc").findPagedList(pageIndex - 1, pageSize).list
}