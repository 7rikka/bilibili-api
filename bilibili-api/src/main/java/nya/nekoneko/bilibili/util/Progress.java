package nya.nekoneko.bilibili.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 进度条
 *
 * @author Rikka
 */
@Slf4j
public class Progress {
    /**
     * 上传开始时间
     */
    private final LocalDateTime start = LocalDateTime.now();
    /**
     * 文件总大小
     */
    private final long fileSize;
    private final int chunkTotal;
    /**
     * 已上传大小
     */
    private long uploadedSize = 0;
    private int chunkComplete = 0;

    public Progress(long fileSize, int chunkTotal) {
        this.fileSize = fileSize;
        this.chunkTotal = chunkTotal;
    }

    public synchronized void add(int size, String result) {
        uploadedSize += size;
        chunkComplete += 1;
        String s;
        if (null != result) {
            //已完成,总量,进度,完成分块数,平均速度
            s = String.format("[%s @ %s][%s][%s][%s][result:%s]",
                    StatUtil.convertFileSize(uploadedSize),
                    StatUtil.convertFileSize(fileSize),
                    String.format("%.2f", 1.0 * uploadedSize / fileSize * 100) + "%",
                    String.format("%d/%d", chunkComplete, chunkTotal),
                    StatUtil.convertSpeed(start, LocalDateTime.now(), uploadedSize),
                    result);
        } else {
            s = String.format("[%s @ %s][%s][%s][%s]",
                    StatUtil.convertFileSize(uploadedSize),
                    StatUtil.convertFileSize(fileSize),
                    String.format("%.2f", 1.0 * uploadedSize / fileSize * 100) + "%",
                    String.format("%d/%d", chunkComplete, chunkTotal),
                    StatUtil.convertSpeed(start, LocalDateTime.now(), uploadedSize));

        }
        log.info(s);
    }

    public void add(int size) {
        add(size, null);
    }
}
