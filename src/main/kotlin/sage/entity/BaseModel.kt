package sage.entity

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.WhenCreated
import com.avaje.ebean.annotation.WhenModified
import sage.domain.commons.DomainException
import sage.domain.commons.IdCommons
import java.sql.Timestamp
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version
import kotlin.reflect.KClass

@MappedSuperclass
abstract class BaseModel : Model() {

  @Id @GeneratedValue
  var id: Long = 0

  @Version
  var version: Long = 0

  @WhenCreated
  var whenCreated: Timestamp? = null

  @WhenModified
  var whenModified: Timestamp? = null

  override fun equals(other: Any?): Boolean{
    if (this === other) return true
    if (other?.javaClass != javaClass) return false
    other as BaseModel
    if (!IdCommons.equal(id, other.id)) return false
    return true
  }

  override fun hashCode(): Int{
    return id.hashCode()
  }
}

fun <T: Any> load(beanType: KClass<T>, id: Long): T? =
    if (id > 0) Model.db().find(beanType.java, id) else null

fun <T : Any> getNonNull(beanType: KClass<T>, id: Long): T = Model.db().find(beanType.java, id)
    ?: throw DomainException("${beanType.java.simpleName}[$id] does not exist")