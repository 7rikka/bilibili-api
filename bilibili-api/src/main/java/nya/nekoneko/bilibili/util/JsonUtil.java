package nya.nekoneko.bilibili.util;

import org.noear.snack.ONode;

public class JsonUtil {
    public static Integer safeGetInt(ONode node, String key) {
        return node.get(key).isNull() ? null : node.get(key).getInt();
    }

    public static String safeGetString(ONode node, String key) {
        return node.get(key).isNull() ? null : node.get(key).getString();
    }

    /**
     * 获取code值
     *
     * @param node
     * @return
     */
    public static Integer safeGetCode(ONode node) {
        return safeGetInt(node, "code");
    }

    /**
     * 获取message值
     *
     * @param node
     * @return
     */
    public static String safeGetMessage(ONode node) {
        return safeGetString(node, "message");
    }
}
