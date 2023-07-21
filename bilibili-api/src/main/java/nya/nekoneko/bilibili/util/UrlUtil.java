package nya.nekoneko.bilibili.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URL处理工具类
 *
 * @author Rikka
 */
public class UrlUtil {

    /**
     * URL编码
     *
     * @param s
     * @return
     */
    public static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * URL解码
     *
     * @param s
     * @return
     */
    public static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    /**
     * 从URL中获取参数值
     *
     * @param url       URL地址
     * @param paramName 参数名
     * @return 参数值，如果未找到返回null
     */
    public static String getParam(String url, String paramName) {
        String[] split1 = url.split("\\?");
        //不包含Param 直接返回null
        if (split1.length == 1) {
            return null;
        }
        for (String s : split1[1].split("&")) {
            String[] split = s.split("=");
            if (split[0].equals(paramName)) {
                return UrlUtil.decode(split[1]);
            }
        }
        return null;
    }
}
