package nya.nekoneko.bilibili.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BiliArchiveStatusEnum {
    /**
     * 审核进行中
     */
    IS_PUBING("is_pubing"),
    /**
     * 已通过审核
     */
    PUBED("pubed"),
    /**
     * 未通过审核
     */
    NOT_PUBED("not_pubed"),
    /**
     * 全部
     */
    ALL("is_pubing,pubed,not_pubed");

    private final String value;

}
