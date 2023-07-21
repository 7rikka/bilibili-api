package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录二维码信息
 *
 * @author Rikka
 */
@Data
@AllArgsConstructor
public class BiliLoginQrInfo {
    private String url;
    private String qrCodeKey;
}
