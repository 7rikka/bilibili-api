package nya.nekoneko.bilibili.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noear.snack.annotation.ONodeAttr;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiliAppSplash {
    /**
     *
     */
    @ONodeAttr(name = "id")
    private Integer id;
    /**
     *
     */
    @ONodeAttr(name = "thumb")
    private String thumb;
    /**
     *
     */
    @ONodeAttr(name = "thumb_hash")
    private String thumbHash;
    /**
     *
     */
    @ONodeAttr(name = "thumb_size")
    private Integer thumbSize;
    /**
     *
     */
    @ONodeAttr(name = "logo_url")
    private String logoUrl;
    /**
     *
     */
    @ONodeAttr(name = "logo_hash")
    private String logoHash;
    /**
     *
     */
    @ONodeAttr(name = "logo_size")
    private Integer logoSize;
    /**
     *
     */
    @ONodeAttr(name = "mode")
    private String mode;
    /**
     *
     */
    @ONodeAttr(name = "thumb_name")
    private String thumbName;
    /**
     *
     */
    @ONodeAttr(name = "source")
    private String source;
    /**
     *
     */
    @ONodeAttr(name = "show_logo")
    private Boolean showLogo;
}
