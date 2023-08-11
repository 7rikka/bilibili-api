package nya.nekoneko.bilibili.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
public enum BiliVideoQuality {
    //127	:	超高清 8K
    //120	:	超清 4K
    //116   :   高清 1080P60
    //112	:	高清 1080P+
    //80	:	高清 1080P
    //64	:	高清 720P
    //32	:	清晰 480P
    //16	:	流畅 360P
    Q_8K(127, "hdflv2","超高清 8K"),
    Q_DOLBY_VISION(126, "hdflv2","杜比视界"),
    Q_HDR(125, "hdflv2","真彩 HDR"),
    Q_4K(120, "hdflv2","超清 4K"),
    Q_1080P60(116, "flv_p60","高清 1080P60"),
    Q_1080P_PLUS(112, "hdflv2","高清 1080P+"),
    Q_1080P(80, "flv","高清 1080P"),
    Q_720P(64, "flv720","高清 720P"),
    Q_480P(32, "flv480","清晰 480P"),
    Q_360P(16, "mp4","流畅 360P");
    @Getter
    private int code;
    @Getter
    private String format;
    @Getter
    private String desc;

    public static BiliVideoQuality of( int code ) {
        BiliVideoQuality[] enumConstants = BiliVideoQuality.class.getEnumConstants();
        for (BiliVideoQuality enumConstant : enumConstants) {
            if (enumConstant.code == code) {
                return enumConstant;
            }
        }
        throw new RuntimeException("Error Video Quality Code: "+code);
    }
}
