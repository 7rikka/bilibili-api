package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nya.nekoneko.bilibili.model.enums.BiliWatermarkPosition;

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
    /**
     * 当前用户uid
     */
    private Long uid;
    /**
     * 当前用户名称
     */
    private String uname;
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     *
     */
    private Integer type;
    /**
     * 水印位置
     * 左上：top-left
     * 右上：top-right
     * 左下：bottom-left
     * 右下：bottom-right
     */
    private BiliWatermarkPosition position;
    /**
     * 水印图地址
     */
    private String url;
    /**
     *
     */
    private String md5;
    /**
     * 水印图宽度（px）
     */
    private Integer width;
    /**
     * 水印图高度（px）
     */
    private Integer height;
    /**
     *
     */
    private LocalDateTime ctime;
    /**
     *
     */
    private LocalDateTime mtime;
}
