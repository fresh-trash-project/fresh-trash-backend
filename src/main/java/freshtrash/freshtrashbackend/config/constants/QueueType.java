package freshtrash.freshtrashbackend.config.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueType {
    PRODUCT_TRANSACTION_COMPLETE("queue.product.complete", "product.productDeal.complete"),
    PRODUCT_CHANGE_SELL_STATUS("queue.product.changeStatus", "product.change.sellStatus"),
    PRODUCT_TRANSACTION_FLAG("queue.product.flag", "product.productDeal.flag"),
    CHAT("queue.chat", "chats.#");

    private final String name;
    private final String routingKey;
}
