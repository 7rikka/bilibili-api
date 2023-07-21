package nya.nekoneko.bilibili.componet.uploader;

import lombok.Data;

/**
 * @author Rikka
 */
public enum UploadLine {
    CS_BDA2("bda2", "cs"),
    CS_QN("qn", "cs"),
    CS_BLDSA("bldsa", "cs"),
    CS_WS("ws", "cs");
    public final String upcdn;
    public final String zone;


    UploadLine(String upcdn, String zone) {
        this.upcdn = upcdn;
        this.zone = zone;
    }
}
