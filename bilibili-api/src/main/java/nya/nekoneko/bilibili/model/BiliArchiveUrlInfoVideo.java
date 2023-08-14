package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nya.nekoneko.bilibili.model.enums.BiliVideoCodec;
import nya.nekoneko.bilibili.model.enums.BiliVideoQuality;

import java.util.List;

/**
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliArchiveUrlInfoVideo {
    private BiliVideoQuality quality;
    private List<String> urlList;
    private int width;
    private int height;
    private String frameRate;
    private BiliVideoCodec codec;
}
