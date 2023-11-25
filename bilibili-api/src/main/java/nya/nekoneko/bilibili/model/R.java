package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    private Integer code;
    private String message;
    private T result;
    private BiliPageInfo pageInfo;
    private String raw;

    public R(Integer code, String message, T result, String raw) {
        this.code = code;
        this.message = message;
        this.result = result;
        this.raw = raw;
    }
}
