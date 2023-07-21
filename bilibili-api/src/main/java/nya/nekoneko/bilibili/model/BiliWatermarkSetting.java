package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliWatermarkSetting {
    private Integer id;
    private Long uid;
    private String uname;
    private Integer state;
    private Integer type;
    private Integer position;
    private String url;
    private String md5;
    private Integer width;
    private Integer height;
    private LocalDateTime ctime;
    private LocalDateTime mtime;
}
