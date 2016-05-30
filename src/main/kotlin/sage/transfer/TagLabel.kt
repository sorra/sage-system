package sage.transfer

import sage.entity.Tag

class TagLabel {
  var id: Long = 0
  var name: String = ""
  var isCore: Boolean = false
  var chainStr: String = name

  internal constructor() {
  }

  constructor(tag: Tag) {
    id = tag.id
    name = tag.name
    isCore = tag.isCore
    chainStr = topDownChainStr(tag.chainUp().map { it.name })
  }

  constructor(tagCard: TagCard) {
    id = tagCard.id
    name = tagCard.name
    isCore = tagCard.isCore
    chainStr = topDownChainStr(tagCard.chainUp.map { it.name })
  }

  override fun toString() = String.format("%s[id=%s, name=%s]", javaClass.simpleName, id, name)

  private fun topDownChainStr(namesBottomUp: List<String>): String {
    if (namesBottomUp.isEmpty()) return ""
    val sb = StringBuilder()
    for (i in namesBottomUp.indices.reversed()) {
      sb.append(namesBottomUp[i])
      if (i > 0)
        sb.append("->")
    }
    return sb.toString()
  }
}
