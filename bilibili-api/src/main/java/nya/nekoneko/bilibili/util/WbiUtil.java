package nya.nekoneko.bilibili.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static nya.nekoneko.bilibili.util.BiliUtil.getTs;

public class WbiUtil {
    private static final String imgKey = "7cd084941338484aae1ad9425b84077c";
    private static final String subKey = "4932caff0ff746eab6f01bf08b70ac45";
    private static final int[] mixinKeyEncTab = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };

    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] result = new char[messageDigest.length * 2];
            for (int i = 0; i < messageDigest.length; i++) {
                result[i * 2] = hexDigits[(messageDigest[i] >> 4) & 0xF];
                result[i * 2 + 1] = hexDigits[messageDigest[i] & 0xF];
            }
            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++)
            key.append(s.charAt(mixinKeyEncTab[i]));
        return key.toString();
    }

    public static String encodeURIComponent(Object o) {
        return URLEncoder.encode(o.toString(), StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static void sign(Map<String, String> map) {
        String mixinKey = getMixinKey(imgKey, subKey);
        map.put("wts", getTs());
        String param = new TreeMap<>(map)
                .entrySet()
                .stream()
                .map(it -> String.format("%s=%s", it.getKey(), encodeURIComponent(it.getValue())))
                .collect(Collectors.joining("&"));
        String wbiSign = md5(param + mixinKey);
        map.put("w_rid", wbiSign);
    }
}
