package nya.nekoneko.bilibili.test;

import nya.nekoneko.bilibili.core.BiliRequestFactor;
import nya.nekoneko.bilibili.model.BiliLoginCredential;
import nya.nekoneko.bilibili.model.BiliVideoPlayUrlInfo;
import nya.nekoneko.bilibili.model.BiliVideoUrlInfoAudio;
import nya.nekoneko.bilibili.model.BiliVideoUrlInfoVideo;
import nya.nekoneko.bilibili.model.enums.BiliAudioQuality;
import nya.nekoneko.bilibili.model.enums.BiliVideoCodec;
import nya.nekoneko.bilibili.model.enums.BiliVideoQuality;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class T2 {
    public static void main(String[] args) {
        BiliLoginCredential credential = new BiliLoginCredential();
        credential.setSessData("");

        String s = BiliRequestFactor.getBiliRequest()
                .url("https://api.bilibili.com/x/player/wbi/playurl")
                .addParam("avid", "565251563")
//                .addParam("bvid", "BV1qM4y1w716")
                .addParam("cid", "965617731")
                .addParam("qn", "127")
                .addParam("fnval", "4048")
//                .addParam("fourk", "1")
                .cookie(credential)
                .buildRequest()
                .doCallGetString();
        System.out.println(s);
        ONode data = ONode.loadStr(s).get("data");
        List<BiliVideoQuality> list = new ArrayList<>();
        for (int i = 0; i < data.get("accept_quality").count(); i++) {
            list.add(BiliVideoQuality.of(data.get("accept_quality").get(i).getInt()));
        }
        //视频流
        ONode dash = data.get("dash");
        ONode video = dash.get("video");
        List<BiliVideoUrlInfoVideo> videoList = new ArrayList<>();
        for (int i = 0; i < video.count(); i++) {
            ONode v = video.get(i);
            List<String> backupUrl = v.get("backup_url").toObjectList(String.class);
            backupUrl.add(0, v.get("base_url").getString());
            videoList.add(BiliVideoUrlInfoVideo.builder()
                    .quality(BiliVideoQuality.of(v.get("id").getInt()))
                    .urlList(backupUrl)
                    .width(v.get("width").getInt())
                    .height(v.get("height").getInt())
                    .frameRate(v.get("frame_rate").getString())
                    .codec(BiliVideoCodec.of(v.get("codecid").getInt()))
                    .build());
        }
        //音频流
        List<BiliVideoUrlInfoAudio> audioList = new ArrayList<>();
        ONode audio = dash.get("audio");
        for (int i = 0; i < audio.count(); i++) {
            ONode a = audio.get(i);
            List<String> backupUrl = a.get("backup_url").toObjectList(String.class);
            backupUrl.add(0, a.get("base_url").getString());
            audioList.add(BiliVideoUrlInfoAudio.builder()
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
            audioList.add(BiliVideoUrlInfoAudio.builder()
                    .quality(BiliAudioQuality.of(db.get("id").getInt()))
                    .urlList(backupUrl)
                    .build());
        }
        //无损音轨
        ONode flac = dash.get("flac");
        if (flac.get("display").getBoolean()) {
            ONode flacAudio = flac.get("audio");
            List<String> backupUrl = flacAudio.get("backup_url").toObjectList(String.class);
            backupUrl.add(0, flacAudio.get("base_url").getString());
            audioList.add(BiliVideoUrlInfoAudio.builder()
                    .quality(BiliAudioQuality.of(flacAudio.get("id").getInt()))
                    .urlList(backupUrl)
                    .build());
        }
        audioList.sort((o1, o2) -> o2.getQuality().getCode() - o1.getQuality().getCode());
        BiliVideoPlayUrlInfo playUrlInfo = BiliVideoPlayUrlInfo.builder()
                .currentQuality(BiliVideoQuality.of(data.get("quality").getInt()))
                .length(data.get("timelength").getInt())
                .acceptQuality(list)
                .videoList(videoList)
                .audioList(audioList)
                .build();
        System.out.println(playUrlInfo);


    }
}
