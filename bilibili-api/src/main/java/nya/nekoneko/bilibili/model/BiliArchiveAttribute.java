package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 稿件属性
 *
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliArchiveAttribute {
    private Integer is360;
    private Integer isCoop;
    private Integer isDolby;
    private Integer isDynamic;
    private Integer isOwner;
    private Integer isPlayed;
    private Integer isPremiere;
    private Integer live;
    private Integer losslessMusic;
    private Integer noPublic;
}
