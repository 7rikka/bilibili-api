package nya.nekoneko.bilibili.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间处理工具类
 *
 * @author Rikka
 */
public class TimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间戳转LocalDateTime
     *
     * @param timestamp 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(Integer timestamp) {
        return Timestamp.from(Instant.ofEpochSecond(timestamp)).toLocalDateTime();
    }
    public static LocalDateTime stringToLocalDateTime(String s) {
        String text = s.split("\\+")[0].replaceAll("T", " ");
        return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
    }
}
