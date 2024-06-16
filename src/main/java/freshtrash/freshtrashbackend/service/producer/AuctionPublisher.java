package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AlarmPayload;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.AUCTION_BID_COMPLETE;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionPublisher {
    private final MQPublisher mqPublisher;

    public void notCompleteBid(Auction auction) {
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AlarmPayload.ofAuctionNotBid(
                        String.format(NOT_COMPLETED_AUCTION.getMessage(), auction.getTitle()), auction)));
    }

    public void completeBid(Auction auction, Long bidMemberId) {
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AlarmPayload.ofAuctionBidByBuyer(
                        String.format(COMPLETE_BID_AUCTION.getMessage(), auction.getTitle()), auction, bidMemberId)));
    }

    public void requestPay(Auction auction, Long bidMemberId) {
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AlarmPayload.ofAuctionBidBySeller(
                        String.format(REQUEST_PAY_AUCTION.getMessage(), auction.getTitle()), auction, bidMemberId)));
    }
}
