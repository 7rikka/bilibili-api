package nya.nekoneko.bilibili.componet.uploader;

/**
 * 上传线路
 *
 * @author Rikka
 */
public enum UploadLine {
    CS_BLDSA("cs", "bldsa"),
    CS_BDA2("cs", "bda2"),
    CS_TX("cs", "tx"),
    CS_TXA("cs", "txa"),
    CS_ALIA("cs", "alia"),
    JD_BLDSA("jd", "bldsa"),
    JD_BD("jd", "bd"),
    JD_TX("jd", "tx"),
    JD_TXA("jd", "txa"),
    JD_ALIA("jd", "alia");

    public final String zone;
    public final String upcdn;

    UploadLine(String zone, String upcdn) {
        this.zone = zone;
        this.upcdn = upcdn;
    }
}
