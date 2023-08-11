package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nya.nekoneko.bilibili.model.enums.BiliVideoQuality;

import java.util.List;

/**
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliVideoPlayUrlInfo {
    private BiliVideoQuality currentQuality;
    private Integer length;
    private List<BiliVideoQuality> acceptQuality;
    private List<BiliVideoUrlInfoVideo> videoList;
    private List<BiliVideoUrlInfoAudio> audioList;
}
