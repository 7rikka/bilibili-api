package nya.nekoneko.bilibili.core;

import nya.nekoneko.bilibili.model.BiliLoginCredential;
import nya.nekoneko.bilibili.model.BiliResult;
import okhttp3.*;
import org.noear.snack.ONode;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static nya.nekoneko.bilibili.util.AppUtil.getSign;


/**
 * 包装一次请求
 *
 * @author Rikka
 */
public class BiliRequest {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType XML = MediaType.parse("application/xml");
    private static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");
    private final Request.Builder builder = new Request.Builder();
    /**
     * 本次请求的Parameter
     */
    private final Map<String, String> paramMap = new HashMap<>();
    private Request request;
    /**
     * 本次请求的URL
     */
    private String url;

    public BiliRequest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加Parameter
     *
     * @param key
     * @param value
     * @return
     */
    public BiliRequest addParam(String key, Object value) {
        if (null != value) {
//            paramMap.put(key, UrlUtil.encode(String.valueOf(value)));
            paramMap.put(key, String.valueOf(value));
        }
        return this;
    }

    public BiliRequest addParams(Map<String, String> params) {
        if (null != params) {
            params.entrySet().stream().forEach(entry -> {
                paramMap.put(entry.getKey(), entry.getValue());
            });
        }
        return this;
    }

    /**
     * 生成完整的URL
     *
     * @param url
     * @param params
     */
    private void addUrl(String url, Map<String, String> params) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(httpUrl).newBuilder();
        if (null != params) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        builder.url(httpBuilder.build());
    }

    /**
     * 设定为GET请求
     *
     * @return
     */
    public BiliRequest get() {
        builder.get();
        return this;
    }
    public BiliRequest post() {
        RequestBody body = RequestBody.create("{}", JSON);
        builder.post(body);
        return this;
    }
    /**
     * 提交Json
     *
     * @param json
     * @return
     */
    public BiliRequest postJson(String json) {
        RequestBody body = RequestBody.create(json, JSON);
        builder.post(body);
        return this;
    }

    /**
     * 提交XML
     *
     * @param xml
     * @return
     */
    public BiliRequest postXml(String xml) {
        RequestBody body = RequestBody.create(xml, XML);
        builder.post(body);
        return this;
    }

    /**
     * 提交表单
     *
     * @param form
     * @return
     */
    public BiliRequest postForm(Map<String, Object> form) {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, Object> item : form.entrySet()) {
            if (null == item.getValue()) {
                //跳过null值
                continue;
            }
            formBody.add(item.getKey(), item.getValue().toString());
        }
        builder.post(formBody.build());
        return this;
    }

    public BiliRequest uploadFile(InputStream inputStream, String fileName, Map<String, String> form) throws IOException {
        RequestBody requestBody = RequestBody.create(inputStream.readAllBytes(), OCTET_STREAM);
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file_up", fileName, requestBody);
        for (Map.Entry<String, String> stringStringEntry : form.entrySet()) {
            multipartBodyBuilder.addFormDataPart(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        builder.post(multipartBodyBuilder.build());
        return this;
    }

    public BiliRequest post(byte[] b) {
        builder.post(RequestBody.create(b, OCTET_STREAM));
        return this;
    }

    public BiliRequest put(byte[] b) {
        builder.put(RequestBody.create(b, OCTET_STREAM));
        return this;
    }

    public BiliRequest cookie(BiliLoginCredential credential) {
        if (null != credential) {
            String cookie = "";
            if (null != credential.getDedeUserId()) {
                cookie = cookie + "DedeUserID=" + credential.getDedeUserId() + "; ";
            }
            if (null != credential.getSessData()) {
                cookie = cookie + "SESSDATA=" + credential.getSessData() + "; ";
            }
            if (null != credential.getBiliJct()) {
                cookie = cookie + "bili_jct=" + credential.getBiliJct() + "; ";
            }
            builder.header("Cookie", cookie.strip());
        }
        return this;
    }

    /**
     * APP用
     *
     * @param credential
     * @return
     */
    public BiliRequest appSign(BiliLoginCredential credential,String appSec) {
        if (null != credential) {
//            if (null != loginInfo.getAccessKey()) {
//                addParam("access_key", loginInfo.getAccessKey());
//            }
        }
        String sign = getSign(paramMap, appSec);
        addParam("sign", sign);
        return this;
    }

    //    public BiliRequest appSign(BilibiliLoginInfo loginInfo, String appSecret) {
//        if (null != loginInfo) {
//            if (null != loginInfo.getAccessKey()) {
//                addParam("access_key", loginInfo.getAccessKey());
//            }
//            String sign = getSign(paramMap, appSecret);
//            addParam("sign", sign);
//        }
//        return this;
//    }
    public BiliRequest buildRequest() {
        addUrl(this.url, this.paramMap);
        addCommonHeaders();
        request = builder.build();
//        System.out.println(request);
        return this;
    }

    /**
     * 添加一些通用的Header
     */
    private void addCommonHeaders() {
        builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Edg/98.0.1108.62");
    }

    /**
     * 添加Header
     */
    public BiliRequest header(String name, String value) {
        builder.header(name, value);
        return this;
    }

    /**
     * 发起请求 获取结果Json
     */
    public String doCallGetString() {
        return Call.doCallGetString(request);
    }
    public ONode doCallGetNode() {
        return ONode.loadStr(Call.doCallGetString(request));
    }

    public BiliResult doCall() {
//        System.out.println(request);
        return Call.doCall(request);
    }
    public Response doCallGetResponse() {
//        System.out.println(request);
        return Call.doCallGetResponse(request);
    }
    public InputStream doCallGetInputStream(){
        return Call.doCallGetInputStream(request);
    }
}
