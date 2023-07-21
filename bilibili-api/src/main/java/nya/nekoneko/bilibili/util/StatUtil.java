package nya.nekoneko.bilibili.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 统计工具类
 *
 * @author Rikka
 */
@Slf4j
public class StatUtil {
    private static final String[] SIZE_UNIT = {"Byte", "KB", "MB", "GB", "TB", "PB", "EB"};
    private static final String[] TIME_UNIT = {"秒", "分钟", "小时", "天"};
    private static final String[] SPEED_UNIT = {"Byte/s", "KB/s", "MB/s", "GB/s"};

    /**
     * 打印统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param fileSize  文件大小（字节数）
     */
    public static void printUploadInfoStat(LocalDateTime startTime, LocalDateTime endTime, long fileSize) {
        log.info("文件大小: {}", convertFileSize(fileSize));
        log.info("上传用时: {}", convertTime(startTime, endTime));
        log.info("平均速度: {}", convertSpeed(startTime, endTime, fileSize));
    }

    /**
     * 转换文件大小
     *
     * @param fileSize 文件大小（字节数）
     * @return 显示的文本，文件大小，保留两位小数，带对应的单位（Byte、KB、MB、GB、TB、PB、EB）
     */
    public static String convertFileSize(long fileSize) {
        double r = 1.0 * fileSize;
        int index = 0;
        while (r >= 1024) {
            r = r / 1024;
            index += 1;
        }
        return String.format("%.2f", r) + SIZE_UNIT[index];
    }

    /**
     * 转换时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 显示的文本，转换后的时间，保留两位小数，加上对应的单位（秒、分钟、小时、天）
     */
    public static String convertTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new RuntimeException("startTime > endTime");
        }
        //用时(秒)
        double t = 1.0 * ChronoUnit.MILLIS.between(startTime, endTime) / 1000;
        int unitIndex = 0;
        if (t >= 60) {
            t /= 60;
            unitIndex++;
            if (t >= 60) {
                t /= 60;
                unitIndex++;
                if (t >= 24) {
                    t /= 24;
                    unitIndex++;
                }
            }
        }
        return String.format("%.2f", t) + TIME_UNIT[unitIndex];
    }

    /**
     * 转换文件上传/下载的速度
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param fileSize  文件大小（字节数）
     * @return 显示的文本，上传/下载的速度，保留两位小数，带对应的单位（Byte/s、KB/s、MB/s、GB/s）
     */
    public static String convertSpeed(LocalDateTime startTime, LocalDateTime endTime, long fileSize) {
        double t = 1.0 * ChronoUnit.MILLIS.between(startTime, endTime) / 1000;
        double r = fileSize / t;
        int index = 0;
        while (r >= 1024) {
            r = r / 1024;
            index += 1;
        }
        return String.format("%.2f", r) + SPEED_UNIT[index];
    }
}
