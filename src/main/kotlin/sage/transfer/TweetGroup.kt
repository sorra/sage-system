package sage.transfer

import sage.annotation.KotlinNoArg
import java.util.*

@KotlinNoArg
class TweetGroup private constructor(): Item {

  val forwards = ArrayList<TweetView>()

  private var origin: TweetView? = null

  /** origin是否被信息流直接收听到了  */
  var containsOrigin = false

  override fun getType() = "TweetGroup"

  override fun getTags() = origin?.tags

  override fun getOrigin() = origin

  fun addForward(forward: TweetView) {
    forward.clearOrigin()
    forwards += forward
  }

  fun addOrigin(origin: TweetView) {
    this.origin = origin
    containsOrigin = true
  }

  companion object {
    fun createByForward(forward: TweetView): TweetGroup {
      val group = TweetGroup()
      group.origin = forward.origin
      group.addForward(forward)
      return group
    }

    fun createByOrigin(origin: TweetView): TweetGroup {
      val group = TweetGroup()
      group.addOrigin(origin)
      return group
    }
  }
}