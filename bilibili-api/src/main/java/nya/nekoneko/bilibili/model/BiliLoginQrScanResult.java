package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录二维码扫描结果
 *
 * @author Rikka
 */
@Data
@AllArgsConstructor
public class BiliLoginQrScanResult {
    /**
     * 二维码扫描状态
     */
    private BiliLoginQrScanState qrScanState;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 登录凭证
     */
    private BiliLoginCredential credential;

    public enum BiliLoginQrScanState {
        /**
         * 成功
         */
        OK,
        /**
         * 未扫码
         */
        NO_SCAN,
        /**
         * 二维码已扫码未确认
         */
        NO_CONFIRM,
        /**
         * 二维码已失效
         */
        QR_EXPIRED
    }
}
