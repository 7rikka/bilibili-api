package nya.nekoneko.bilibili.core;

import kotlin.Pair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nya.nekoneko.bilibili.model.BiliResult;
import okhttp3.*;
import org.noear.snack.ONode;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.Iterator;

/**
 * 发起网络请求
 */
@Data
@Slf4j
public class Call {
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .readTimeout(Duration.ofSeconds(100))
            .connectTimeout(Duration.ofSeconds(100))
            .callTimeout(Duration.ofSeconds(100))
            .build();
    private static final int SUCCESS = 200;
//    static {
//        client.dispatcher().setMaxRequestsPerHost(16);
//        client.dispatcher().setMaxRequests(16);
//    }
//    private static ProxyProvider proxyProvider;

//    public static void setProxyProvider(ProxyProvider proxyProvider) {
//        Call.proxyProvider = proxyProvider;
//    }

    public static BiliResult doCall(Request request) {
        String result = doCallGetString(request);
//        System.out.println(result);
        return ONode.deserialize(result, BiliResult.class);
    }

    public static String doCallGetString(Request request) {
        try {
            Response response = client.newCall(request).execute();
//            Headers headers = response.headers();
//            Iterator<Pair<String, String>> iterator = headers.iterator();
//            while (iterator.hasNext()) {
//                Pair<String, String> next = iterator.next();
//                System.out.println(next);
//            }
            if (SUCCESS != response.code()) {
                String body = null;
                if (null != response.body()) {
                    body = response.body().string();
                }
                throw new RequestException(request, response, "HTTP CODE: " + response.code(), body);
            }
            ResponseBody body = response.body();
            if (null == body) {
                throw new RequestException(request, response, "Body为空");
            }
//            BiliRequestHandler.process(response.headers());
            return body.string().strip();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(request, null, e.getMessage());
        }
    }

    public static byte[] doCallGetBytes(Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (200 != response.code() && 206 != response.code()) {
//                System.out.println(response.body().string());
                throw new RequestException(request, response, "HTTP CODE: " + response.code());
            }
            ResponseBody body = response.body();
            if (null == body) {
                throw new RequestException(request, response, "Body为空");
            }
            return body.bytes();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(request, null, e.getMessage());
        }
    }

    public static InputStream doCallGetInputStream(Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (200 != response.code() && 206 != response.code()) {
//                System.out.println(response.body().string());
                throw new RequestException(request, response, "HTTP CODE: " + response.code());
            }
            ResponseBody body = response.body();
            if (null == body) {
                throw new RequestException(request, response, "Body为空");
            }
            return body.byteStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(request, null, e.getMessage());
        }
    }

    public static String doCallGetHeader(Request request, String headerName) {
        try {
            Response response = client.newCall(request).execute();
            if (200 != response.code()) {
//                System.out.println(response.body().string());
                throw new RequestException(request, response, "HTTP CODE: " + response.code());
            }
            return response.header(headerName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(request, null, e.getMessage());
        }
    }

    public static Response doCallGetResponse(Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (SUCCESS != response.code()) {
                String body = null;
                if (null != response.body()) {
                    body = response.body().string();
                }
                throw new RequestException(request, response, "HTTP CODE: " + response.code(), body);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(request, null, e.getMessage());
        }
    }

//    public static BiliResult doCallWithProxy(Request request) {
//        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
//                .readTimeout(Duration.ofSeconds(100))
//                .connectTimeout(Duration.ofSeconds(100))
//                .callTimeout(Duration.ofSeconds(100));
//        String proxy = proxyProvider.getProxy();
//        if (null != proxy) {
//            log.info("使用代理: {}", proxy);
//            String[] split = proxy.split(":");
//            String ip = split[0];
//            int port = Integer.parseInt(split[1]);
//            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
//        }
//        OkHttpClient client1 = builder.build();
//        try {
//            Response response = client1.newCall(request).execute();
//            if (SUCCESS != response.code()) {
//                String body = null;
//                if (null != response.body()) {
//                    body = response.body().string();
//                }
//                throw new RequestException(request, response, "HTTP CODE: " + response.code(), body);
//            }
//            ResponseBody body = response.body();
//            if (null == body) {
//                throw new RequestException(request, response, "Body为空");
//            }
//            BiliRequestHandler.process(response.headers());
//            String result = body.string().strip();
//            log.info(result);
//            BiliResult biliResult = ONode.deserialize(result, BiliResult.class);
//            biliResult.setRaw(result);
//            return biliResult;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RequestException(request, null, e.getMessage());
//        }
//    }
//    public static String doCallWithProxyGetString(Request request) {
//        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
//                .readTimeout(Duration.ofSeconds(100))
//                .connectTimeout(Duration.ofSeconds(100))
//                .callTimeout(Duration.ofSeconds(100));
//        String proxy = proxyProvider.getProxy();
//        if (null != proxy) {
//            log.info("使用代理: {}", proxy);
//            String[] split = proxy.split(":");
//            String ip = split[0];
//            int port = Integer.parseInt(split[1]);
//            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
//        }
//        OkHttpClient client1 = builder.build();
//        try {
//            Response response = client1.newCall(request).execute();
//            if (SUCCESS != response.code()) {
//                String body = null;
//                if (null != response.body()) {
//                    body = response.body().string();
//                }
//                throw new RequestException(request, response, "HTTP CODE: " + response.code(), body);
//            }
//            ResponseBody body = response.body();
//            if (null == body) {
//                throw new RequestException(request, response, "Body为空");
//            }
//            BiliRequestHandler.process(response.headers());
//            return body.string().strip();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RequestException(request, null, e.getMessage());
//        }
//    }
}
