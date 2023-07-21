package nya.nekoneko.bilibili.model;

import lombok.Data;
import org.noear.snack.ONode;

import java.util.List;

/**
 * @author Rikka
 */
@Data
public class BiliResult {
    /**
     * 业务响应码
     */
    private Integer code;
    /**
     *
     */
    private String message;
    /**
     * 实际数据ONode对象
     */
    private ONode data;
    /**
     *
     */
    private String msg;
    /**
     * 本次请求结果的原始信息
     */
    private String raw;

    /**
     * 转换为数据对象
     *
     * @param tClass 对象类型
     * @return 数据对象
     */
    public <T> T toData(Class<T> tClass) {
        return data.toObject(tClass);
    }

    /**
     * 转换为数据对象列表
     *
     * @param tClass 对象类型
     * @return 数据对象列表
     */
    public <T> List<T> toDataList(Class<T> tClass) {
        return data.toObjectList(tClass);
    }

}
