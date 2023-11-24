package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 稿件信息
 *
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliArchive {
    private Integer aid;
    private String bvid;
    private Integer tid;
    private String title;
    private String cover;
    private String tag;
    private Integer duration;
    /**
     * 稿件类型
     * 1：自制
     * 2：转载
     */
    private Integer copyright;
    /**
     * 是否显示 未经作者授权禁止转载
     * 0：不显示
     * 1：显示
     */
    private boolean noReprint;
    /**
     * 稿件简介
     */
    private String desc;
    private Integer state;
    private String stateDesc;
    /**
     * 转载来源
     */
    private String source;
    /**
     * 粉丝动态
     */
    private String dynamic;
    private LocalDateTime ptime;
    private LocalDateTime ctime;
    /**
     * 分p列表
     */
    private List<BiliArchiveVideo> videos;
    /**
     *
     */
//    private String reject_reason;
//    private String reject_reason_url;
}
