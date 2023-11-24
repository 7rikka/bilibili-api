package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noear.snack.annotation.ONodeAttr;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliPartInfo {
    private long cid;
    private int page;
    private String from;
    private String part;
    private int duration;
    //    private String vid;
    //    private String weblink;
    private BiliVideoDimension dimension;
    @ONodeAttr(name = "first_frame")
    private String firstFrame;
}
