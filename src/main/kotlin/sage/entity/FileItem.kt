package sage.entity

import javax.persistence.Entity

@Entity
class FileItem(
    var name: String?,
    var webPath: String?,
    var storePath: String?,
    var ownerId: Long?) : AutoModel() {

  companion object : BaseFind<Long, FileItem>(FileItem::class)
}