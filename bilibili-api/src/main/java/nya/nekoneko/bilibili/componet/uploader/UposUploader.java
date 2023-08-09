package nya.nekoneko.bilibili.componet.uploader;

import lombok.extern.slf4j.Slf4j;
import nya.nekoneko.bilibili.core.BiliRequestFactor;
import nya.nekoneko.bilibili.model.BiliLoginCredential;
import nya.nekoneko.bilibili.model.BiliUploadResult;
import nya.nekoneko.bilibili.util.Progress;
import nya.nekoneko.bilibili.util.StatUtil;
import org.noear.snack.ONode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Ho
 */
@Slf4j
public class UposUploader {
    /**
     * 线程池活动任务数检测间隔
     */
    private static final int CHECK_INTERVAL = 1000;
    private static final int DEFAULT_THREAD_COUNT = 16;

    public BiliUploadResult upload(BiliLoginCredential credential, File file, UploadLine line, int threadCount) throws Exception {
        if (!file.exists()) {
            throw new RuntimeException("需要上传的文件不存在");
        }
        LocalDateTime start = LocalDateTime.now();
        String fileName = file.getName();
        log.info("使用 UposUploader(cdn：{} zone：{}) 上传", line.upcdn, line.zone);
        long fileSize = file.length();
        //STEP1.获取上传信息
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/preupload")
                .addParam("name", fileName)
                .addParam("r", "upos")
                .addParam("profile", "ugcupos/bup")
                .addParam("ssl", "0")
                .addParam("version", "2.14.0.0")
                .addParam("build", "2140000")
                .addParam("size", fileSize)
                .addParam("webVersion", "2.14.0")
                .addParam("os", "upos")
                .addParam("zone", line.zone)
                .addParam("upcdn", line.upcdn)
                .get()
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        log.info("preupload：{}", result);
        ONode node = ONode.loadStr(result);
        String auth = node.get("auth").getString();
        int bizId = node.get("biz_id").getInt();
        int chunkSize = node.get("chunk_size").getInt();
        String endpoint = node.get("endpoint").getString();
        String uposUri = node.get("upos_uri").getString();
        int chunkNum = (int) Math.ceil(1.0 * fileSize / chunkSize);
        log.info("文件名: " + fileName);
        log.info("分块数: " + chunkNum);
        log.info("文件大小: " + StatUtil.convertFileSize(fileSize));
        log.info("分块大小: " + StatUtil.convertFileSize(chunkSize));
        log.info("线程数: " + threadCount);

        List<String> endpoints = node.get("endpoints").toObjectList(String.class);
        //选择指定的节点
        for (String s : endpoints) {
            if (s.contains(line.upcdn)) {
                endpoint = s;
            }
        }
        String basicUrl = "https:" + endpoint + "/" + uposUri.substring("upos://".length());
        log.info("目标地址: " + basicUrl);
//        //STEP.2
//        put("uploads", "");
//        put("output", "json");
        String result2 = BiliRequestFactor.getBiliRequest()
                .url(basicUrl)
                .addParam("uploads", "")
                .addParam("output", "json")
                .postForm(new HashMap<>())
                .header("X-Upos-Auth", auth)
                .buildRequest()
                .doCallGetString();
        log.info("result2:" + result2);
        ONode node2 = ONode.loadStr(result2);
        String uploadId = node2.get("upload_id").getString();
        String bucket = node2.get("bucket").getString();
        String key = node2.get("key").getString();
        String fns = key.substring("/".length(), key.indexOf("."));
        log.info("fns: " + fns);
//        //STEP3.开始上传
        log.info("STEP3.开始上传");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        Progress progress = new Progress(fileSize,chunkNum);
        for (int chunkIndex = 0; chunkIndex < chunkNum; chunkIndex++) {
            byte[] b = new byte[chunkSize];
            int read = bis.read(b);
            if (read < chunkSize) {
                //最后一个分块
                b = Arrays.copyOf(b, read);
            }
            byte[] finalB = b;
            //参数
            Map<String, String> params = new HashMap<>();
            params.put("partNumber", String.valueOf(chunkIndex + 1));
            params.put("uploadId", uploadId);
            params.put("chunk", String.valueOf(chunkIndex));
            params.put("chunks", String.valueOf(chunkNum));
            params.put("size", String.valueOf(b.length));
            params.put("start", String.valueOf(chunkIndex * chunkSize));
            params.put("end", String.valueOf(chunkIndex * chunkSize + b.length));
            params.put("total", String.valueOf(fileSize));
            String info = String.format("[%s/%s]", chunkIndex + 1, chunkNum);
            while (service.getActiveCount() >= threadCount) {
                //如果活动线程数小于并发数, 继续添加任务, 解决堆内存溢出问题
                // java.lang.OutOfMemoryError: Java heap space
                Thread.sleep(CHECK_INTERVAL);
            }
            service.execute(() -> {
                uploadChunk(basicUrl, params, auth, finalB, info, progress);
            });
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);
//        //SETP4.上传结束
        ONode array = ONode.newArray();
        for (int i = 0; i < chunkNum; i++) {
            array.add(ONode.newObject()
                    .set("partNumber", i)
                    .set("eTag", "eTag")
            );
        }
        ONode data = ONode.newObject()
                .setNode("parts", array);
        String result4 = BiliRequestFactor.getBiliRequest()
                .url(basicUrl)
                .addParam("output", "json")
                .addParam("name", fileName)
                .addParam("profile", "ugcupos/bup")
                .addParam("uploadId", uploadId)
                .addParam("biz_id", bizId)
                .postJson(data.toString())
                .header("X-Upos-Auth", auth)
                .buildRequest()
                .doCallGetString();
        log.info("result4: " + result4);

        StatUtil.printUploadInfoStat(start, LocalDateTime.now(), fileSize);
        return new BiliUploadResult(bizId, fns);
    }

    public BiliUploadResult upload(BiliLoginCredential credential, String filePath, UploadLine line, int threadCount) throws Exception {
        return upload(credential, new File(filePath), line, DEFAULT_THREAD_COUNT);
    }

    public BiliUploadResult upload(BiliLoginCredential credential, File file, UploadLine line) throws Exception {
        return upload(credential, file, line, DEFAULT_THREAD_COUNT);
    }

    public BiliUploadResult upload(BiliLoginCredential credential, String filePath, UploadLine line) throws Exception {
        return upload(credential, new File(filePath), line, DEFAULT_THREAD_COUNT);
    }

    private void uploadChunk(String basicUrl, Map<String, String> params, String auth, byte[] data, String info, Progress progress) {

        while (true) {

            try {
                String request = BiliRequestFactor.getBiliRequest()
                        .url(basicUrl)
                        .addParams(params)
                        .put(data)
                        .header("X-Upos-Auth", auth)
                        .buildRequest()
                        .doCallGetString();
                //进度条+1
//                progress.add(data.length, request);
                progress.add(data.length);
                break;
            } catch (Exception e) {
                log.info(info + "发生异常, 3秒后重新上传.");
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
