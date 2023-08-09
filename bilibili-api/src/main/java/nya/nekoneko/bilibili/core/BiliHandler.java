package nya.nekoneko.bilibili.core;

/**
 * @author Rikka
 */
public interface BiliHandler {
    default void onSend() {
    }

    default void onComplete() {
    }

    default void onError() {
    }
}
