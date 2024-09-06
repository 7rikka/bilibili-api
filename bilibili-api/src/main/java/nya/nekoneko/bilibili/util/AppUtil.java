package nya.nekoneko.bilibili.util;


import cn.hutool.crypto.SecureUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Ho
 */
public class AppUtil {
    public static String getSign(Map<String, String> map, String appSecret) {
        StringJoiner sj = new StringJoiner("&");
        Object[] keyArray = map.keySet().toArray();
        Arrays.sort(keyArray);
        for (Object key : keyArray) {
            sj.add(key + "=" + UrlUtil.encode(map.get((String) key)));
        }
        return SecureUtil.md5(sj + appSecret);
    }
}
