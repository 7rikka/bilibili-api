package nya.nekoneko.bilibili.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Rikka
 */

@Getter
@AllArgsConstructor
public enum BiliAudioQuality {
    Q_FLAC(30251, "无损音频"),
    Q_DOLBY_ATMOS(30250, "杜比全景声"),
    Q_192K(30280, "192K"),
    Q_132K(30232, "132K"),
    Q_64K(30216, "64K");
    private int code;
    private String desc;

    public static BiliAudioQuality of(int code) {
        BiliAudioQuality[] enumConstants = BiliAudioQuality.class.getEnumConstants();
        for (BiliAudioQuality enumConstant : enumConstants) {
            if (enumConstant.code == code) {
                return enumConstant;
            }
        }
        throw new RuntimeException("Error Audio Quality Code: "+code);
    }
}
