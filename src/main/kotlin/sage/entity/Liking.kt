package sage.entity

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.WhenCreated
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Liking(
    @Id @GeneratedValue
    var id: Long = 0,
    @WhenCreated
    var whenCreated: Timestamp? = null,
    var userId: Long = 0,
    var
) : Model()