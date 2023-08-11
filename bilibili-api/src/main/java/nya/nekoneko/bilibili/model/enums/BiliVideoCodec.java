package nya.nekoneko.bilibili.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BiliVideoCodec {
    AVC(7, "AVC"),
    HEVC(12, "HEVC"),
    AV1(13, "AV1");
    @Getter
    private int code;
    @Getter
    private String desc;

    public static BiliVideoCodec of(int code ) {
        BiliVideoCodec[] enumConstants = BiliVideoCodec.class.getEnumConstants();
        for (BiliVideoCodec enumConstant : enumConstants) {
            if (enumConstant.code == code) {
                return enumConstant;
            }
        }
        throw new RuntimeException("Error Video Codec Code.");
    }
}
