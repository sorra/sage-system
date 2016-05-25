package sage.entity

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.WhenCreated
import com.avaje.ebean.annotation.WhenModified
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Version

@Entity
class LoginPass(
    @Id
    var passId: String,

    var userId: Long,

    var whenToExpire: Timestamp
) : Model() {
  @Version
  var version: Long = 0

  @WhenCreated
  var whenCreated: Timestamp? = null

  @WhenModified
  var whenModified: Timestamp? = null

  companion object : Find<String, LoginPass>()
}