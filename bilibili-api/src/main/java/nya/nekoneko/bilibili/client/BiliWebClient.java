package nya.nekoneko.bilibili.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nya.nekoneko.bilibili.core.BiliRequestFactor;
import nya.nekoneko.bilibili.model.*;
import nya.nekoneko.bilibili.model.enums.BiliWatermarkPosition;
import nya.nekoneko.bilibili.util.TimeUtil;
import nya.nekoneko.bilibili.util.UrlUtil;
import org.noear.snack.ONode;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author Rikka
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BiliWebClient extends BiliClient {
    /**
     * 登录凭证
     */
    public BiliLoginCredential credential;

    /**
     * 获取Web端扫码登录信息
     *
     * @return
     */
    public BiliLoginQrInfo getLoginQrCode() {
        BiliResult result = BiliRequestFactor.getBiliRequest()
                .url("https://passport.bilibili.com/x/passport-login/web/qrcode/generate")
                .addParam("source", "main-fe-header")
                .buildRequest()
                .doCall();
        return new BiliLoginQrInfo(result.getData().get("url").getRawString(), result.getData().get("qrcode_key").getRawString());
    }

    /**
     * 获取二维码扫描结果
     *
     * @param qrCodeKey 二维码Key
     * @return 扫描结果
     */
    public BiliLoginQrScanResult getLoginQrScanResult(String qrCodeKey) {
        BiliResult result = BiliRequestFactor.getBiliRequest()
                .url("https://passport.bilibili.com/x/passport-login/web/qrcode/poll")
                .addParam("qrcode_key", qrCodeKey)
                .addParam("source", "main-fe-header")
                .buildRequest()
                .doCall();
        ONode data = result.getData();
        //0：成功
        //86101：未扫码
        //86038：二维码已失效
        //86090：二维码已扫码未确认
        int code = data.get("code").getInt();
        String message = data.get("message").getString();
        if (0 == code) {
            String url = data.get("url").getString();
            String refreshToken = data.get("refresh_token").getString();
            BiliLoginCredential credential = BiliLoginCredential.builder()
                    .dedeUserId(UrlUtil.getParam(url, "DedeUserID"))
                    .dedeUserIdCkMd5(UrlUtil.getParam(url, "DedeUserID__ckMd5"))
                    .expires(UrlUtil.getParam(url, "Expires"))
                    .sessData(UrlUtil.getParam(url, "SESSDATA"))
                    .biliJct(UrlUtil.getParam(url, "bili_jct"))
                    .refreshToken(refreshToken)
                    .build();
            return new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.OK, message, credential);
        } else if (86101 == code) {
            return new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.NO_SCAN, message, null);
        } else if (86038 == code) {
            return new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.QR_EXPIRED, message, null);
        } else if (86090 == code) {
            return new BiliLoginQrScanResult(BiliLoginQrScanResult.BiliLoginQrScanState.NO_CONFIRM, message, null);
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * 获取分p的封面列表
     *
     * @param fns 分p的filename
     * @return 封面地址列表
     */
    public List<String> getArchiveRecoverList(String fns) {
        BiliResult result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/vupre/web/archive/recovers")
                .addParam("fns", fns)
                .get()
                .cookie(credential)
                .buildRequest()
                .doCall();
        return result.getData().toObjectList(String.class);
    }

    public BiliArchive getArchiveDetail(Integer aid, String bvid) {
        BiliResult result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/vupre/web/archive/view")
                .addParam("aid", aid)
                .addParam("bvid", bvid)
                .get()
                .cookie(credential)
                .buildRequest()
                .doCall();
        ONode data = result.getData();
        BiliArchive.builder()
                .build();
        ONode videosNode = data.get("videos");
        videosNode.forEach(n -> {
            BiliArchiveVideo.builder()
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
                    .createTime(TimeUtil.timestampToLocalDateTime(n.get("ctime").getInt()))
                    .build();


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
        return null;
    }

    public BiliArchive getArchiveDetail(Integer aid) {
        return getArchiveDetail(aid, null);
    }

    public BiliArchive getArchiveDetail(String bvid) {
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
        if (archive.isNoReprint()) {
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
            videoNode.set("desc", video.getDesc());
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
            if (setting.isSelectionReply()) {
                node.set("up_selection_reply", true);
            }
            if (setting.isCloseReply()) {
                node.set("up_close_reply", true);
            }
            if (setting.isCloseDanmaku()) {
                node.set("up_close_danmu", true);
            }
            if (setting.isOpenElecPanel()) {
                node.set("open_elec", 1);
            }
            if (setting.isEnableDolby()) {
                node.set("dolby", 1);
            }
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
     * 获取个人水印配置
     *
     * @return
     */
    public BiliWatermarkSetting getWatermarkSetting() {
        String result = BiliRequestFactor.getBiliRequest()
                .url("https://member.bilibili.com/x/web/watermark")
                .addParam("csrf", credential.getBiliJct())
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        result = result.replaceAll("\\\\\"", "\"")
                .replaceAll("\"\\{", "{")
                .replaceAll("\\}\"", "}");
        ONode node = ONode.loadStr(result);
        ONode d = node.get("data");
        int position = d.get("position").getInt();
        BiliWatermarkPosition p;
        switch (position) {
            case 1 -> p = BiliWatermarkPosition.TOP_LEFT;
            case 2 -> p = BiliWatermarkPosition.TOP_RIGHT;
            case 3 -> p = BiliWatermarkPosition.BOTTOM_LEFT;
            case 4 -> p = BiliWatermarkPosition.BOTTOM_RIGHT;
            default -> p = null;
        }
        return BiliWatermarkSetting.builder()
                .id(d.get("id").getInt())
                .uid(d.get("mid").getLong())
                .uname(d.get("uname").getString())
                .enable(d.get("state").getInt() == 1)
                .type(d.get("type").getInt())
                .position(p)
                .url(d.get("url").getString())
                .md5(d.get("md5").getString())
                .width(d.get("info").get("width").getInt())
                .height(d.get("info").get("height").getInt())
                .ctime(TimeUtil.stringToLocalDateTime(d.get("ctime").getString()))
                .mtime(TimeUtil.stringToLocalDateTime(d.get("mtime").getString()))
                .build();
    }

    private BiliArchiveStat getArchiveStat(Integer aid, String bvid) {
        String s = BiliRequestFactor.getBiliRequest()
                .url("https://api.bilibili.com/x/web-interface/archive/stat")
                .addParam("aid", aid)
                .addParam("bvid", bvid)
                .buildRequest()
                .doCallGetString();
        ONode node = ONode.loadStr(s);
        ONode n = node.get("data");
        return BiliArchiveStat.builder()
                .aid(n.get("aid").getInt())
                .bvid(n.get("bvid").getString())
                .view(n.get("view").getInt())
                .danmaku(n.get("danmaku").getInt())
                .reply(n.get("reply").getInt())
                .favorite(n.get("favorite").getInt())
                .coin(n.get("coin").getInt())
                .share(n.get("share").getInt())
                .like(n.get("like").getInt())
                .nowRank(n.get("now_rank").getInt())
                .hisRank(n.get("his_rank").getInt())
                .noReprint(n.get("no_reprint").getInt())
                .copyright(n.get("copyright").getInt())
                .build();

    }

    public BiliArchiveStat getArchiveStat(Integer aid) {
        return getArchiveStat(aid, null);
    }

    public BiliArchiveStat getArchiveStat(String bvid) {
        return getArchiveStat(null, bvid);
    }
}


