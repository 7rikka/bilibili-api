package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime expires;
    private String sessData;
    private String biliJct;
    private String accessToken;
    private String refreshToken;
}
