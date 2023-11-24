package nya.nekoneko.bilibili.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BiliArchiveOrderType {
    /**
     * 播放数
     */
    PLAY("click"),
    /**
     * 收藏数
     */
    FAVORITE("stow"),
    /**
     * 弹幕数
     */
    DANMAKU("dm_count"),
    /**
     * 播放数
     */
    REPLY("scores"),
    /**
     * 投稿时间
     */
    DATETIME("senddate");

    private final String value;
}
