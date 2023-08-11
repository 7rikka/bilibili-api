package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nya.nekoneko.bilibili.model.enums.BiliAudioQuality;
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
public class BiliVideoUrlInfoAudio {
    private BiliAudioQuality quality;
    private List<String> urlList;
}
