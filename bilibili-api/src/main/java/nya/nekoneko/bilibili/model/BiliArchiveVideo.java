package nya.nekoneko.bilibili.model;

/**
 * @author Rikka
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 稿件分p信息
 *
 * @author Rikka
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliArchiveVideo {
    /**
     * 分p标题
     */
    private String title;
    /**
     * 分p的fns
     */
    private String filename;
    /**
     *
     */
    private Integer cid;
    /**
     *
     */
    private Integer duration;
    /**
     *
     */
    private Integer index;
    private Integer status;
    private String statusDesc;
    private String rejectReason;
    private String rejectReasonUrl;
    private String modifyAdvise;
    private String problemDescription;
    private String problemDescriptionTitle;
    private Integer rejectReasonId;
    /**
     * 0：
     * 1：
     * 9：
     * 15：
     */
    private Integer failCode;
    private String failDesc;
    private Integer xCodeState;
    private LocalDateTime ctime;
}
