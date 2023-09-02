package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author takan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliVideoDimension {
    private int width;
    private int height;
    private int rotate;
}