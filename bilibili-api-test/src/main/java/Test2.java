import nya.nekoneko.bilibili.componet.uploader.UploadLine;

public class Test2 {
    public static void main(String[] args) {
        //https://upos-cs-upcdnbldsa.bilivideo.com/OK?line=0.1
        for (UploadLine line : UploadLine.values()) {
            String url = "https://upos-" + line.zone + "-upcdn" + line.upcdn + ".bilivideo.com";
            System.out.println(url);
        }
    }
}
