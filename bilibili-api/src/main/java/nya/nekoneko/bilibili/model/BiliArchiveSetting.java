package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 投稿配置
 *
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliArchiveSetting {
    /**
     * 预约发布时间
     * 需转换为时间戳
     */
    private LocalDateTime scheduledTime;
    /**
     * 是否发起预约
     * 0：不发起 1：发起
     */
    private boolean actReserveCreate;
    /**
     * 是否允许二创
     * -1：不允许 1：允许
     */
    private boolean allowRecreate;
    /**
     * 是否开启杜比音效
     * 0：不开启 1：开启
     */
    private boolean enableDolby;
    /**
     * 是否开启Hi-Res无损音质
     * 0：不开启 1：开启
     */
    private boolean enableLosslessMusic;
    /**
     * 是否开启充电面板
     * 0：不开启 1：开启
     */
    private boolean openElecPanel;
    /**
     * UP关闭评论
     * false：不关闭 true：关闭
     */
    private boolean closeReply;
    /**
     * UP关闭弹幕
     * false：不关闭 true：关闭
     */
    private boolean closeDanmaku;
    /**
     * UP开启精选评论
     * false：不开启 true：开启
     */
    private boolean selectionReply;
}
