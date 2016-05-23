package sage.web.context

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

object DateUtil {
  @JvmStatic fun humanTime(time: Date?): String? {
    if (time == null) {
      return null
    }
    val instant = Instant.ofEpochMilli(time.time)
    val minutes = instant.until(Instant.now(), ChronoUnit.MINUTES)
    val thatTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val thatDay = thatTime.toLocalDate()

    if (minutes == 0L) {
      return "刚才"
    }
    if (minutes > 0 && minutes < 60) {
      return "${minutes}分钟前"
    }
    if (LocalDate.now().isEqual(thatDay)) { // 当天
      return DateTimeFormatter.ofPattern("HH:mm").cn().format(thatTime)
    }
    if (LocalDate.now().year == thatDay.year) { // 当年
      return DateTimeFormatter.ofPattern("MM-dd HH:mm").cn().format(thatTime)
    }
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").cn().format(thatTime)
  }

  @JvmStatic fun spanHumanTime(time: Date): String {
    return String.format("<span class=\"human-time\">%s</span>", humanTime(time))
  }

  fun DateTimeFormatter.cn() = withZone(ZoneId.of("UTC+8"))
}
