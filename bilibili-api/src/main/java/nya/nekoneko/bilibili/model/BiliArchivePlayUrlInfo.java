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
public class BiliArchivePlayUrlInfo {
    private BiliVideoQuality currentQuality;
    private Integer length;
    private List<BiliVideoQuality> acceptQuality;
    private List<BiliArchiveUrlInfoVideo> videoList;
    private List<BiliArchiveUrlInfoAudio> audioList;
}
