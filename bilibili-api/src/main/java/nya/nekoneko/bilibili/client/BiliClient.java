package nya.nekoneko.bilibili.client;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nya.nekoneko.bilibili.core.BiliException;
import nya.nekoneko.bilibili.core.BiliRequestFactor;
import nya.nekoneko.bilibili.model.*;
import nya.nekoneko.bilibili.model.enums.*;
import nya.nekoneko.bilibili.util.TimeUtil;
import nya.nekoneko.bilibili.util.UrlUtil;
import org.noear.snack.ONode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static nya.nekoneko.bilibili.util.BiliUtil.getTs;
import static nya.nekoneko.bilibili.util.JsonUtil.safeGetCode;
import static nya.nekoneko.bilibili.util.JsonUtil.safeGetMessage;

/**
 * @author Rikka
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiliClient {
    /**
     * 登录凭证
     */
    public BiliLoginCredential credential;

    private String appKey;
    private String appSec;

    /**
     * 获取Web端登录二维码
     *
     * @return
     */
    public R<BiliLoginQrInfo> getLoginQrCode() {
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://passport.bilibili.com/x/passport-login/web/qrcode/generate")
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        ONode dataNode = node.get("data");
        return new R<>(
                safeGetCode(node),
                safeGetMessage(node),
                new BiliLoginQrInfo(dataNode.get("url").getString(), dataNode.get("qrcode_key").getString()),
                result);
    }

    /**
     * 获取二维码扫描结果
     *
     * @param qrCodeKey 二维码Key
     * @return 扫描结果
     */
    public R<BiliLoginQrScanResult> getLoginQrScanResult(String qrCodeKey) {
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://passport.bilibili.com/x/passport-login/web/qrcode/poll")
                .addParam("qrcode_key", qrCodeKey)
                .addParam("source", "main-fe-header")
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        ONode dataNode = node.get("data");
        //0：成功
        //86101：未扫码
        //86038：二维码已失效
        //86090：二维码已扫码未确认
        int code = dataNode.get("code").getInt();
        String message = dataNode.get("message").getString();
        BiliLoginQrScanResult r;
        if (0 == code) {
            String url = dataNode.get("url").getString();
            String refreshToken = dataNode.get("refresh_token").getString();
            BiliLoginCredential credential = BiliLoginCredential.builder()
                    .dedeUserId(UrlUtil.getParam(url, "DedeUserID"))
                    .dedeUserIdCkMd5(UrlUtil.getParam(url, "DedeUserID__ckMd5"))
                    .expires(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(UrlUtil.getParam(url, "Expires"))), ZoneId.systemDefault()))
                    .sessData(UrlUtil.getParam(url, "SESSDATA"))
                    .biliJct(UrlUtil.getParam(url, "bili_jct"))
                    .refreshToken(refreshToken)
                    .build();
            r = new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.OK, message, credential);
        } else if (86101 == code) {
            r = new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.NO_SCAN, message, null);
        } else if (86038 == code) {
            r = new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.QR_EXPIRED, message, null);
        } else if (86090 == code) {
            r = new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.NO_CONFIRM, message, null);
        } else {
            throw new RuntimeException("Error Code: " + code);
        }
        return new R<>(safeGetCode(node), safeGetMessage(node), r, result);
    }

    /**
     * 获取分p的封面列表
     *
     * @param fns 分p的filename
     * @return 封面地址列表，最多9条
     */
    public R<List<String>> getArchiveRecoverList(String... fns) {
        StringJoiner fnsString = new StringJoiner(",");
        Arrays.stream(fns).forEach(fnsString::add);
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/vupre/web/archive/recovers")
                .addParam("fns", fnsString.toString())
                .get()
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        if (0 == code) {
            List<String> list = node.get("data").toObjectList(String.class);
            return new R<>(code, message, list, result);
        } else {
            return new R<>(code, message, null, result);
        }

    }

    private R<BiliArchive> getArchiveDetail(Long aid, String bvid) {
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/vupre/web/archive/view")
                .addParam("aid", aid)
                .addParam("bvid", bvid)
                .get()
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        System.out.println(result);
        if (0 == code) {
            ONode data = node.get("data");
            //archive
            ONode archive = data.get("archive");
            List<BiliArchiveVideo> videoList = new ArrayList<>();
            BiliArchive archive1 = BiliArchive.builder()
                    .aid(archive.get("aid").getLong())
                    .bvid(archive.get("bvid").getString())
                    .tid(archive.get("tid").getInt())
                    .title(archive.get("title").getString())
                    .cover(archive.get("cover").getString())
                    .rejectReason(archive.get("reject_reason").getString())
                    .rejectReasonUrl(archive.get("reject_reason_url").getString())
                    .tag(archive.get("tag").getString())
                    .duration(archive.get("duration").getInt())
                    .copyright(archive.get("copyright").getInt())
                    .noReprint(archive.get("no_reprint").getInt())
                    .desc(archive.get("desc").getString())
                    .state(archive.get("state").getInt())
                    .stateDesc(archive.get("state_desc").getString())
                    .source(archive.get("source").getString())
                    .descFormatId(archive.get("desc_format_id").getInt())
                    .dynamic(archive.get("dynamic").getRawString())
                    .ptime(TimeUtil.timestampToLocalDateTime(archive.get("ptime").getInt()))
                    .ctime(TimeUtil.timestampToLocalDateTime(archive.get("ctime").getInt()))
                    .videos(videoList)
                    .build();

            //videos
            ONode videosNode = data.get("videos");
            videosNode.forEach(n -> {
                videoList.add(BiliArchiveVideo.builder()
                        .title(n.get("title").getString())
                        .filename(n.get("filename").getString())
                        .cid(n.get("cid").getInt())
                        .duration(n.get("duration").getInt())
                        .index(n.get("index").getInt())
                        .status(n.get("status").getInt())
                        .statusDesc(n.get("status_desc").getString())
                        .rejectReason(n.get("reject_reason").getString())
                        .rejectReasonUrl(n.get("reject_reason_url").getString())
                        .failCode(n.get("fail_code").getInt())
                        .failDesc(n.get("fail_desc").getString())
                        .xCodeState(n.get("xcode_state").getInt())
                        .ctime(TimeUtil.timestampToLocalDateTime(n.get("ctime").getInt()))
                        .build());


                //fail_code
                //0 正常
                //1
                //2
                //6
                //9
                //11
                //14
                //15
                //16

                //xcode_state
                //0
                //1
                //2
                //3
                //6

                //status
                //-100      已删除
                //-30
                //-16
                //-4        撞车
                //-2
                //-1
                //0
                //10000
            });
            return new R<>(code, message, archive1, result);
        } else {
            return new R<>(code, message, null, result);
        }
    }

    public R<BiliArchive> getArchiveDetail(Long aid) {
        return getArchiveDetail(aid, null);
    }

    public R<BiliArchive> getArchiveDetail(String bvid) {
        return getArchiveDetail(null, bvid);
    }

    /**
     * 提交稿件
     *
     * @param archive 稿件信息
     * @param setting 投稿设置
     */
    public void submitArchive(BiliArchive archive, BiliArchiveSetting setting) {
        //{
        //    "cover": "https://archive.biliimg.com/bfs/archive/e534778a1fe9fade2b247f7e9a7cc818bdb90130.jpg",
        //    "title": "1674292159_2",
        //    "copyright": 1,
        //    "tid": 65,
        //    "tag": "桌游棋牌",
        //    "desc_format_id": 32,
        //    "desc": "",
        //    "dynamic": "",
        //    "interactive": 0,
        //    "videos": [
        //        {
        //            "filename": "n230717qn2fkjwn1e86u3t34rdlskrpw",
        //            "title": "1674292159_2",
        //            "desc": "",
        //            "cid": 1199997623
        //        }
        //    ],
        //    "no_disturbance": 0,
        //    "subtitle": {
        //        "open": 0,
        //        "lan": ""
        //    },
        //    "adorder_good_info": {},
        //    "web_os": 1,
        //    "csrf": "ab45362effed48cd6e904010ebc8200f"
        //}
        ONode node = ONode.newObject();
        //稿件标题 必填
        node.set("title", archive.getTitle());
        //稿件分区id 必填
        node.set("tid", archive.getTid());
        //稿件来源类型 1: 自制 2: 转载 必填
        node.set("copyright", archive.getCopyright());
        if (archive.getCopyright() == 1) {
            //是否允许二创
            //-1：不允许 1：允许
            if (setting.isAllowRecreate()) {
                node.set("recreate", 1);
            }
        }
        if (archive.getCopyright() == 2) {
            node.set("source", archive.getSource());
        }
        //稿件Tag 必填
        node.set("tag", archive.getTag());
        //启用未经作者授权 禁止转载? 0: 不启用 1: 启用
        if (archive.getNoReprint() == 1) {
            node.set("no_reprint", 1);
        }
        node.set("cover", archive.getCover());
        node.set("desc", archive.getDesc());
        node.set("dynamic", archive.getDynamic());
        ONode node1 = ONode.newArray();
        for (BiliArchiveVideo video : archive.getVideos()) {
            ONode videoNode = ONode.newObject();
            //必填
            videoNode.set("filename", video.getFilename());
            videoNode.set("title", video.getTitle());
            node1.addNode(videoNode);
        }
        node.set("videos", node1);
        if (null != setting) {
            //定时发布
            LocalDateTime scheduledTime = setting.getScheduledTime();
            if (null != scheduledTime) {
                long l = scheduledTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                node.set("dtime", l);
                //发起预约
                if (setting.isActReserveCreate()) {
                    node.set("act_reserve_create", 1);
                }
            }
            //评论精选
            if (setting.isSelectionReply()) {
                node.set("up_selection_reply", true);
            }
            //关闭评论
            if (setting.isCloseReply()) {
                node.set("up_close_reply", true);
            }
            //关闭弹幕
            if (setting.isCloseDanmaku()) {
                node.set("up_close_danmu", true);
            }
            //启用充电面板
            if (setting.isOpenElecPanel()) {
                node.set("open_elec", 1);
            }
            //杜比音效
            if (setting.isEnableDolby()) {
                node.set("dolby", 1);
            }
            //Hi-Res无损音频
            if (setting.isEnableLosslessMusic()) {
                node.set("lossless_music", 1);
            }

        }
//        System.out.println("提交Json：" + node.toJson());
//        node.set("act_reserve_create", credential.getBiliJct());
        //https://member.bilibili.com/x/vu/web/add
        //https://member.bilibili.com/x/vu/web/add/v3
//        //SUBMIT_ARCHIVE
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/vu/web/add/v3")
                .addParam("csrf", credential.getBiliJct())
                .postJson(node.toString())
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
//        System.out.println(result);
    }

    /**
     * 提交稿件
     *
     * @param archive 稿件信息
     */
    public void submitArchive(BiliArchive archive) {
        submitArchive(archive, null);
    }

    /**
     * 编辑稿件
     *
     * @param archive
     */
    public void editArchiveWeb(BiliArchive archive) {
        ONode n = ONode.newObject();
        n.set("aid", archive.getAid());
        n.set("title", archive.getTitle());
        n.set("cover", archive.getCover());
        n.set("source", archive.getSource());
        n.set("desc_format_id", archive.getDescFormatId());
        n.set("tag", archive.getTag());
        n.set("desc", archive.getDesc());
        n.set("tid", archive.getTid());
        n.set("copyright", archive.getCopyright());
        n.set("dynamic", archive.getDynamic());
        ONode list = ONode.newArray();
        archive.getVideos().forEach(video -> {
            ONode v = ONode.newObject();
            v.set("filename", video.getFilename());
            v.set("title", video.getTitle());
            v.set("cid", video.getCid());
            list.addNode(v);
        });
        n.setNode("videos", list);
        System.out.println("提交Json：" + n.toString());
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/vu/web/edit")
                .addParam("csrf", credential.getBiliJct())
                .postJson(n.toString())
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        System.out.println(result);
    }
    /**
     * 获取个人水印配置
     *
     * @return
     */
//    public BiliWatermarkSetting getWatermarkSetting() {
//        String result = BiliRequestFactor.getBiliRequest()
//                .url("https://member.bilibili.com/x/web/watermark")
//                .addParam("csrf", credential.getBiliJct())
//                .cookie(credential)
//                .buildRequest()
//                .doCallGetString();
//        result = result.replaceAll("\\\\\"", "\"")
//                .replaceAll("\"\\{", "{")
//                .replaceAll("\\}\"", "}");
//        ONode node = ONode.loadStr(result);
//        ONode d = node.get("data");
//        int position = d.get("position").getInt();
//        BiliWatermarkPosition p;
//        switch (position) {
//            case 1 -> p = BiliWatermarkPosition.TOP_LEFT;
//            case 2 -> p = BiliWatermarkPosition.TOP_RIGHT;
//            case 3 -> p = BiliWatermarkPosition.BOTTOM_LEFT;
//            case 4 -> p = BiliWatermarkPosition.BOTTOM_RIGHT;
//            default -> p = null;
//        }
//        return BiliWatermarkSetting.builder()
//                .id(d.get("id").getInt())
//                .uid(d.get("mid").getLong())
//                .uname(d.get("uname").getString())
//                .enable(d.get("state").getInt() == 1)
//                .type(d.get("type").getInt())
//                .position(p)
//                .url(d.get("url").getString())
//                .md5(d.get("md5").getString())
//                .width(d.get("info").get("width").getInt())
//                .height(d.get("info").get("height").getInt())
//                .ctime(TimeUtil.stringToLocalDateTime(d.get("ctime").getString()))
//                .mtime(TimeUtil.stringToLocalDateTime(d.get("mtime").getString()))
//                .build();
//    }
//    @Deprecated
//    private BiliArchiveStat getArchiveStat(Integer aid, String bvid) {
//        if (null != bvid) {
//            checkBvid(bvid);
//        }
//        String s = BiliRequestFactor.getBiliRequest()
//                .url("https://api.bilibili.com/x/web-interface/archive/stat")
//                .addParam("aid", aid)
//                .addParam("bvid", bvid)
//                .buildRequest()
//                .doCallGetString();
//        ONode node = ONode.loadStr(s);
//        ONode n = node.get("data");
//        return BiliArchiveStat.builder()
//                .aid(n.get("aid").getLong())
//                .bvid(n.get("bvid").getString())
//                .view(n.get("view").getInt())
//                .danmaku(n.get("danmaku").getInt())
//                .reply(n.get("reply").getInt())
//                .favorite(n.get("favorite").getInt())
//                .coin(n.get("coin").getInt())
//                .share(n.get("share").getInt())
//                .like(n.get("like").getInt())
//                .nowRank(n.get("now_rank").getInt())
//                .hisRank(n.get("his_rank").getInt())
//                .noReprint(n.get("no_reprint").getInt())
//                .copyright(n.get("copyright").getInt())
//                .build();
//    }
//
//    @Deprecated
//    public BiliArchiveStat getArchiveStat(Integer aid) {
//        return getArchiveStat(aid, null);
//    }
//
//    @Deprecated
//    public BiliArchiveStat getArchiveStat(String bvid) {
//        return getArchiveStat(null, bvid);
//    }

    /**
     * 获取分p播放地址
     *
     * @param aid
     * @param cid
     * @return
     */
    public R<BiliArchivePlayUrlInfo> getArchivePlayUrlInfo(Long aid, Long cid) {
        return getArchivePlayUrlInfo(aid, null, cid);
    }

    /**
     * 获取分p播放地址
     *
     * @param bvid
     * @param cid
     * @return
     */
    public R<BiliArchivePlayUrlInfo> getArchivePlayUrlInfo(String bvid, Long cid) {
        return getArchivePlayUrlInfo(null, bvid, cid);
    }

    /**
     * 获取分p播放地址
     *
     * @param aid
     * @param bvid
     * @param cid
     * @return
     */
    private R<BiliArchivePlayUrlInfo> getArchivePlayUrlInfo(Long aid, String bvid, Long cid) {
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://api.bilibili.com/x/player/wbi/playurl")
                .addParam("avid", aid)
                .addParam("bvid", bvid)
                .addParam("cid", cid)
                .addParam("qn", "127")
                .addParam("fnval", "4048")
//                .addParam("fourk", "1")
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        if (0 == code) {
            ONode data = node.get("data");
            List<BiliVideoQuality> list = new ArrayList<>();
            for (int i = 0; i < data.get("accept_quality").count(); i++) {
                list.add(BiliVideoQuality.of(data.get("accept_quality").get(i).getInt()));
            }
            //视频流
            ONode dash = data.get("dash");
            ONode video = dash.get("video");
            List<BiliArchiveUrlInfoVideo> videoList = new ArrayList<>();
            for (int i = 0; i < video.count(); i++) {
                ONode v = video.get(i);
                List<String> backupUrl = v.get("backup_url").toObjectList(String.class);
                backupUrl.add(0, v.get("base_url").getString());
                videoList.add(BiliArchiveUrlInfoVideo.builder()
                        .quality(BiliVideoQuality.of(v.get("id").getInt()))
                        .urlList(backupUrl)
                        .width(v.get("width").getInt())
                        .height(v.get("height").getInt())
                        .frameRate(v.get("frame_rate").getString())
                        .codec(BiliVideoCodec.of(v.get("codecid").getInt()))
                        .build());
            }
            //音频流
            List<BiliArchiveUrlInfoAudio> audioList = new ArrayList<>();
            ONode audio = dash.get("audio");
            for (int i = 0; i < audio.count(); i++) {
                ONode a = audio.get(i);
                List<String> backupUrl = a.get("backup_url").toObjectList(String.class);
                backupUrl.add(0, a.get("base_url").getString());
                audioList.add(BiliArchiveUrlInfoAudio.builder()
                        .quality(BiliAudioQuality.of(a.get("id").getInt()))
                        .urlList(backupUrl)
                        .build());
            }
            //杜比全景声
            ONode dolby = dash.get("dolby");
            ONode dolbyAudio = dolby.get("audio");
            for (int i = 0; i < dolbyAudio.count(); i++) {
                ONode db = dolbyAudio.get(i);
                List<String> backupUrl = db.get("backup_url").toObjectList(String.class);
                backupUrl.add(0, db.get("base_url").getString());
                audioList.add(BiliArchiveUrlInfoAudio.builder()
                        .quality(BiliAudioQuality.of(db.get("id").getInt()))
                        .urlList(backupUrl)
                        .build());
            }
            //无损音轨
            ONode flac = dash.get("flac");
            if (!flac.get("audio").isNull()) {
                ONode flacAudio = flac.get("audio");
                List<String> backupUrl = flacAudio.get("backup_url").toObjectList(String.class);
                backupUrl.add(0, flacAudio.get("base_url").getString());
                audioList.add(BiliArchiveUrlInfoAudio.builder()
                        .quality(BiliAudioQuality.of(flacAudio.get("id").getInt()))
                        .urlList(backupUrl)
                        .build());
            }
            //排序
            audioList.sort((o1, o2) -> o2.getQuality().getCode() - o1.getQuality().getCode());
            return new R<>(code, message, BiliArchivePlayUrlInfo.builder()
                    .currentQuality(BiliVideoQuality.of(data.get("quality").getInt()))
                    .length(data.get("timelength").getInt())
                    .acceptQuality(list)
                    .videoList(videoList)
                    .audioList(audioList)
                    .build(), result);
        } else {
            return new R<>(code, message, null, result);
        }
    }

    /**
     * 获取分P信息
     *
     * @param aid
     * @return
     */
    public R<List<BiliPartInfo>> getPartList(Long aid) {
        return getPartList(aid, null);
    }

    /**
     * 获取分P信息
     *
     * @param bvid
     * @return
     */
    public R<List<BiliPartInfo>> getPartList(String bvid) {
        return getPartList(null, bvid);
    }

    /**
     * 获取分P信息
     *
     * @param aid
     * @param bvid
     * @return
     */
    private R<List<BiliPartInfo>> getPartList(Long aid, String bvid) {
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://api.bilibili.com/x/player/pagelist")
                .addParam("aid", aid)
                .addParam("bvid", bvid)
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        if (0 == code) {
            return new R<>(code, message, node.get("data").toObjectList(BiliPartInfo.class), result);
        } else {
            return new R<>(code, message, null, result);
        }
    }

    /**
     * 查询我的投稿列表
     *
     * @param page      页数（页数不得大于10000/分页大小，最多获取10000条）
     * @param pageSize  分页大小（范围[1,50]）
     * @param status    稿件状态
     * @param keyword   搜索关键词
     * @param tid       分区id
     * @param orderType 排序方式（均为倒序排列）
     * @return
     */
    public R<List<BiliArchive>> getMyArchiveList(Integer page, Integer pageSize, BiliArchiveStatusEnum status, String keyword, Integer tid, BiliArchiveOrderType orderType) {
        if (null != pageSize && (pageSize <= 0 || pageSize > 50)) {
            throw new BiliException("不正确的分页大小");
        }
        if (null != pageSize) {
            int maxPage = 10000 / pageSize;
            if (page > maxPage) {
                throw new BiliException("页数不得大于" + maxPage);
            }
        }

        //coop: 1
        //interactive: 1
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/web/archives")
                .addParam("status", status.getValue())
                .addParam("pn", page)
                .addParam("ps", pageSize)
                .addParam("keyword", keyword)
                .addParam("tid", tid)
                .addParam("order", orderType)
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);

        if (0 == code) {
            List<BiliArchive> list = new ArrayList<>();
            ONode data = node.get("data");
            ONode archiveListNode = data.get("arc_audits");
            if (!archiveListNode.isNull()) {
                //遍历每个视频
                for (int i = 0; i < archiveListNode.count(); i++) {
                    ONode v = archiveListNode.get(i);
                    ONode archive = v.get("Archive");
//                    System.out.println("title:" + archive.get("title").getRawString());
//                    System.out.println("reject_reason:" + archive.get("reject_reason").getRawString());
//                    System.out.println("reject_reason_url:" + archive.get("reject_reason_url").getRawString());
//                    System.out.println("modify_advise:" + archive.get("modify_advise").getRawString());
//                    System.out.println("problem_description:" + archive.get("problem_description").getRawString());
//                    System.out.println("problem_description_title:" + archive.get("problem_description_title").getRawString());
//                    System.out.println("reject_reason_id:" + archive.get("reject_reason_id").getInt());
//                    System.out.println("state:" + archive.get("state").getString());
//                    System.out.println("state_desc:" + archive.get("state_desc").getRawString());
//                    System.out.println(v.toJson());
//                    System.out.println("========================");
                    List<BiliArchiveVideo> videoList = new ArrayList<>();
                    BiliArchive archive1 = BiliArchive.builder()
                            .aid(archive.get("aid").getLong())
                            .bvid(archive.get("bvid").getRawString())
                            .tid(archive.get("tid").getInt())
                            .title(archive.get("title").getRawString())
                            .cover(archive.get("cover").getRawString())

                            .rejectReason(archive.get("reject_reason").getRawString())
                            .rejectReasonUrl(archive.get("reject_reason_url").getRawString())
                            .modifyAdvise(archive.get("modify_advise").getRawString())
                            .problemDescription(archive.get("problem_description").getRawString())
                            .problemDescriptionTitle(archive.get("problem_description_title").getRawString())
                            .rejectReasonId(archive.get("reject_reason_id").getInt())


                            .tag(archive.get("tag").getRawString())
                            .duration(archive.get("duration").getInt())
                            .copyright(archive.get("copyright").getInt())
                            .noReprint(archive.get("no_reprint").getInt())
                            .desc(archive.get("desc").getRawString())
                            .state(archive.get("state").getInt())
                            .stateDesc(archive.get("state_desc").getRawString())
                            .source(archive.get("source").getRawString())
                            .descFormatId(archive.get("desc_format_id").getInt())
                            .dynamic(archive.get("dynamic").getRawString())
                            .ptime(TimeUtil.timestampToLocalDateTime(archive.get("ptime").getInt()))
                            .ctime(TimeUtil.timestampToLocalDateTime(archive.get("ctime").getInt()))
                            .videos(videoList)
                            .build();

                    list.add(archive1);
                    ONode videos = v.get("Videos");
                    for (int j = 0; j < videos.count(); j++) {
                        ONode video = videos.get(j);
                        videoList.add(
                                BiliArchiveVideo.builder()
                                        .title(video.get("title").getRawString())
                                        .filename(video.get("filename").getRawString())
                                        .cid(video.get("cid").getInt())
                                        .duration(video.get("duration").getInt())
                                        .index(video.get("index").getInt())
                                        .status(video.get("status").getInt())
                                        .statusDesc(video.get("status_desc").getRawString())
                                        .rejectReason(video.get("reject_reason").getRawString())
                                        .rejectReasonUrl(video.get("reject_reason_url").getRawString())
                                        .modifyAdvise(video.get("modify_advise").getRawString())
                                        .problemDescription(video.get("problem_description").getRawString())
                                        .problemDescriptionTitle(video.get("problem_description_title").getRawString())
                                        .rejectReasonId(video.get("reject_reason_id").getInt())
                                        .failCode(video.get("fail_code").getInt())
                                        .failDesc(video.get("fail_desc").getRawString())
                                        .xCodeState(video.get("xcode_state").getInt())
                                        .ctime(TimeUtil.timestampToLocalDateTime(video.get("ctime").getInt()))
                                        .build());

                    }
                    ONode statNode = v.get("stat");
                    if (!statNode.isNull()) {
                        archive1.setStat(BiliArchiveStat.builder()
                                .aid(statNode.get("aid").getLong())
                                .bvid(archive.get("bvid").getRawString())
                                .view(statNode.get("view").getInt())
                                .danmaku(statNode.get("danmaku").getInt())
                                .reply(statNode.get("reply").getInt())
                                .favorite(statNode.get("favorite").getInt())
                                .coin(statNode.get("coin").getInt())
                                .share(statNode.get("share").getInt())
                                .like(statNode.get("like").getInt())
                                .nowRank(statNode.get("now_rank").getInt())
                                .hisRank(statNode.get("his_rank").getInt())
                                .noReprint(archive.get("no_reprint").getInt())
                                .copyright(archive.get("copyright").getInt())
                                .build());
                    }
                }
            }
            //========
            //分页信息
            //========
            ONode pageNode = data.get("page");
            BiliPageInfo pageInfo = null;
            if (!pageNode.isNull()) {
                pageInfo = BiliPageInfo.builder()
                        .pageNum(pageNode.get("pn").getInt())
                        .pageSize(pageNode.get("ps").getInt())
                        .total(pageNode.get("count").getInt())
                        .build();
            }
            return new R<>(code, message, list, pageInfo, result);
        } else {
            //Json格式不正确
            return new R<>(code, message, null, result);
        }
    }

    public void getPrivateMessage(Long talkerId, Integer pageSize, Long maxSeqNo, Long minSeqNo) {
        if (null != pageSize) {
            if (pageSize < 1 || pageSize > 200) {
                throw new BiliException("分页大小取值范围：[1,200]");
            }
        }
        //
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://api.vc.bilibili.com/svr_sync/v1/svr_sync/fetch_session_msgs")
                .addParam("talker_id", talkerId)
                .addParam("session_type", 1)
                .addParam("size", pageSize)
                .addParam("mobi_app", "web")
                .addParam("end_seqno", maxSeqNo)
                .addParam("begin_seqno", minSeqNo)
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
//        System.out.println(result);
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        if (0 == code) {
            ONode data = node.get("data");
            ONode messages = data.get("messages");
            for (int i = 0; i < messages.count(); i++) {
                ONode oNode = messages.get(i).get("msg_type");

                System.out.println(oNode.toJson());
            }
//            System.out.println(messages.count());
//            ONode oNode = data.get("has_more");
//            ONode oNode = data.get("min_seqno");
//            ONode oNode = data.get("max_seqno");
        } else {

        }
    }

    /**
     * @param cid 电话区号
     * @param tel 电话号码
     * @return 验证码key
     */
    public R<String> sendLoginSms(String cid, String tel) {
        if (StrUtil.hasBlank(appKey, appSec)) {
            throw new RuntimeException("未设置AppKey或AppSecret");
        }
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://passport.bilibili.com/x/passport-login/sms/send")
                .addParam("appkey", "1d8b6e7d45233436")
                .addParam("cid", cid)
                .addParam("tel", tel)
                .addParam("ts", getTs())
                .post()
                .appSign(credential, appSec)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        if (0 == code) {
            String captchaKey = node.get("data").get("captcha_key").getString();
            return new R<>(code, message, captchaKey, result);
        } else {
            return new R<>(code, message, null, result);
        }
    }

    /**
     * 使用短信验证码登录
     *
     * @param cid        电话区号
     * @param tel        电话号码
     * @param captchaKey 验证码key
     * @param smsCode    验证码code
     * @return 登录凭证
     */
    public R<BiliLoginCredential> loginSms(String cid, String tel, String captchaKey, String smsCode) {
        if (StrUtil.hasBlank(appKey, appSec)) {
            throw new RuntimeException("未设置AppKey或AppSecret");
        }
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://passport.bilibili.com/x/passport-login/login/sms")
                .addParam("appkey", appKey)
                .addParam("captcha_key", captchaKey)
                .addParam("cid", cid)
                .addParam("code", smsCode)
                .addParam("tel", tel)
                .addParam("ts", getTs())
                .post()
                .appSign(credential, appSec)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(result);
        Integer code = safeGetCode(node);
        String message = safeGetMessage(node);
        if (0 == code) {
            ONode tokenInfo = node.get("data").get("token_info");
            ONode cookies = node.get("data").get("cookie_info").get("cookies");
            BiliLoginCredential credential = BiliLoginCredential.builder()
                    .dedeUserId(cookies.get(2).get("value").getString())
                    .dedeUserIdCkMd5(cookies.get(3).get("value").getString())
                    .expires(LocalDateTime.ofInstant(Instant.ofEpochSecond(cookies.get(0).get("expires").getLong()), ZoneId.systemDefault()))
                    .sessData(cookies.get(0).get("value").getString())
                    .biliJct(cookies.get(1).get("value").getString())
                    .accessToken(tokenInfo.get("access_token").getString())
                    .refreshToken(tokenInfo.get("refresh_token").getString())
                    .build();
            return new R<>(code, message, credential, result);
        } else {
            return new R<>(code, message, null, result);
        }
    }
}


