package freshtrash.freshtrashbackend.config.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueType {
    WASTE_TRANSACTION_COMPLETE("queue.waste.complete", "waste.transaction.complete"),
    WASTE_CHANGE_SELL_STATUS("queue.waste.changeStatus", "waste.change.sellStatus"),
    WASTE_TRANSACTION_FLAG("queue.waste.flag", "waste.transaction.flag");

    private final String name;
    private final String routingKey;
}
