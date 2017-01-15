package sage.transfer

import sage.annotation.KotlinNoArg
import java.util.ArrayList

@KotlinNoArg
class Stream {
  val items: MutableList<Item> = ArrayList()

  constructor(_items: List<Item>) {
    items += _items
  }

  override fun toString(): String {
    return "Stream tweets count: " + items.size
  }
}
