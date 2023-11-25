package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author takan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliArchiveStat {
    private Long aid;
    private String bvid;
    private Integer view;
    private Integer danmaku;
    private Integer reply;
    private Integer favorite;
    private Integer coin;
    private Integer share;
    private Integer like;
    private Integer nowRank;
    private Integer hisRank;
    private Integer noReprint;
    private Integer copyright;
}