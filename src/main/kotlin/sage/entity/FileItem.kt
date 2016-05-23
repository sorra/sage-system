package sage.entity

import javax.persistence.Entity

@Entity
class FileItem(
    var name: String?,
    var webPath: String?,
    var storePath: String?,
    var ownerId: Long?) : BaseModel() {
  companion object : Find<Long, FileItem>()
}