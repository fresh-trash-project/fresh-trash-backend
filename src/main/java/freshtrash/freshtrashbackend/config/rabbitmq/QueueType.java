package freshtrash.freshtrashbackend.config.rabbitmq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueType {
    // Queue
    PRODUCT_TRANSACTION_COMPLETE("queue.product.complete", "product.productDeal.complete"),
    PRODUCT_CHANGE_SELL_STATUS("queue.product.changeStatus", "product.change.sellStatus"),
    PRODUCT_TRANSACTION_FLAG("queue.product.flag", "product.productDeal.flag"),
    CHAT("queue.chat", "chats.#"),
    AUCTION_BID_COMPLETE("queue.auction.complete", "auction.bid.complete"),

    // DLQ
    DLQ_PRODUCT_TRANSACTION_COMPLETE("queue.product.complete.dlq", "product.productDeal.complete"),
    DLQ_PRODUCT_CHANGE_SELL_STATUS("queue.product.changeStatus.dlq", "product.change.sellStatus"),
    DLQ_PRODUCT_TRANSACTION_FLAG("queue.product.flag.dlq", "product.productDeal.flag"),
    DLQ_CHAT("queue.chat.dlq", "chats.#"),
    DLQ_AUCTION_BID_COMPLETE("queue.auction.complete.dlq", "auction.bid.complete"),

    // Parking Lot
    PRODUCT_PARKING_LOT("queue.product.parking-lot", "product.#"),
    CHAT_PARKING_LOT("queue.chat.parking-lot", "chats.#"),
    AUCTION_PARKING_LOT("queue.auction.parking-lot", "auction.#");

    private final String name;
    private final String routingKey;
}
