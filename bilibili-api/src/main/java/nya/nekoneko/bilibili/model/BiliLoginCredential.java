package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录信息
 *
 * @author Rikka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliLoginCredential {
    private String dedeUserId;
    private String dedeUserIdCkMd5;
    private String expires;
    private String sessData;
    private String biliJct;
    private String refreshToken;
}
