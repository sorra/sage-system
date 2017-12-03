package sage.entity

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.WhenCreated
import com.avaje.ebean.annotation.WhenModified
import sage.domain.commons.IdCommons
import java.sql.Timestamp
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
abstract class AutoModel : Model() {
  // AutoModel has auto-increment id & useful stuff

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
    other as AutoModel
    if (!IdCommons.equal(id, other.id)) return false
    return true
  }

  override fun hashCode(): Int{
    return id.hashCode()
  }
}

val siteLaunchTime = 1463989140000 // 2016 5/23 15:39