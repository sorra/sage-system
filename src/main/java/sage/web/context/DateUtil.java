package sage.web.context;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {
  public static String humanTime(Date time) {
    if (time == null) {
      return null;
    }
    Instant instant = Instant.ofEpochMilli(time.getTime());
    long minutes = instant.until(Instant.now(), ChronoUnit.MINUTES);
    LocalDateTime thatTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    LocalDate thatDay = thatTime.toLocalDate();

    if (minutes == 0) {
      return "刚才";
    }
    if (minutes > 0 && minutes < 60) {
      return minutes + "分钟前";
    }
    if (LocalDate.now().isEqual(thatDay)) { // 当天
      return DateTimeFormatter.ofPattern("HH:mm").format(thatTime);
    }
    if (LocalDate.now().getYear() == thatDay.getYear()) { // 当年
      return DateTimeFormatter.ofPattern("MM-dd HH:mm").format(thatTime);
    }
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(thatTime);
  }

  public static String spanHumanTime(Date time) {
    return String.format("<span class=\"human-time\">%s</span>", humanTime(time));
  }
}
