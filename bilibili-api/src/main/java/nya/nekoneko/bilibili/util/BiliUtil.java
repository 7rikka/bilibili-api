package nya.nekoneko.bilibili.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 算法处理类
 *
 * @author Rikka
 */
public class BiliUtil {
    private static final int[] MIXIN_KEY_ENC_TAB = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };
    private static final String TABLE = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";
    private static final int[] S = new int[]{11, 10, 3, 8, 4, 6};
    private static final int XOR = 177451812;
    private static final long ADD = 8728348608L;
    private static final Map<Character, Integer> MAP = new HashMap<>();

    static {
        for (int i = 0; i < 58; i++) {
            MAP.put(TABLE.charAt(i), i);
        }
    }

    /**
     * 将aid转换为bvid
     *
     * @param aid 待转换的aid
     * @return 转换后的bvid
     */
    public static String aidToBvid(int aid) {
        long x = (aid ^ XOR) + ADD;
        char[] chars = new char[]{'B', 'V', '1', ' ', ' ', '4', ' ', '1', ' ', '7', ' ', ' '};
        for (int i = 0; i < 6; i++) {
            int pow = (int) Math.pow(58, i);
            long i1 = x / pow;
            int index = (int) (i1 % 58);
            chars[S[i]] = TABLE.charAt(index);
        }
        return String.valueOf(chars);
    }

    /**
     * 将bvid转换为aid
     *
     * @param bvid 待转换的bvid
     * @return 转换后的aid
     */
    public static int bvidToAid(String bvid) {
        checkBvid(bvid);
        long r = 0;
        for (int i = 0; i < 6; i++) {
            r += (long) (MAP.get(bvid.charAt(S[i])) * Math.pow(58, i));
        }
        return (int) ((r - ADD) ^ XOR);
    }


    /**
     * 检测bvid格式是否正确
     * bvid格式如下
     * | index | 0 | 1 | 2 | 3 | 4 | 5 | 6   | 7 | 8 | 9 | 10 | 11 |
     * | value | B | V | 1 | ? | ? | 4 | 1/y | 1 | ? | 7 | ?  | ?  |
     *
     * @param bvid 待检测的bvid
     */
    public static void checkBvid(String bvid) {
        if (null == bvid
            || !bvid.startsWith("BV")
            || bvid.charAt(2) != '1'
            || bvid.charAt(5) != '4'
            || (bvid.charAt(6) != '1' && bvid.charAt(6) != 'y')
            || bvid.charAt(7) != '1'
            || bvid.charAt(9) != '7'
        ) {
            throw new RuntimeException("bvid格式不正确：" + bvid);
        }

    }

    public static String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            key.append(s.charAt(MIXIN_KEY_ENC_TAB[i]));
        }
        return key.toString();
    }

    /**
     * 获取经过Wbi签名后的Param
     *
     * @param imgKey   imgKey
     * @param subKey   subKey
     * @param paramMap Param参数Map
     * @return 经过Wbi签名后的Param
     */
    public static String getWbiSignParam(String imgKey, String subKey, Map<String, Object> paramMap) {
        //添加当前时间戳
        paramMap.put("wts", System.currentTimeMillis() / 1000);
        String mixinKey = getMixinKey(imgKey, subKey);
        StringJoiner param = new StringJoiner("&");
        //排序 + 拼接字符串
        paramMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> param.add(entry.getKey() + "=" + UrlUtil.encode(entry.getValue().toString())));
        String s = param + mixinKey;
        String wbiSign = generateMD5(s);
        return param + "&w_rid=" + wbiSign;

    }

    private static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
