package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AuctionAlarmPayload;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.*;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionPublisher {
    private final MQPublisher mqPublisher;

    public void publishToSellerForNotExistBidders(Auction auction) {
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AuctionAlarmPayload.ofNotExistBidders(
                        String.format(NOT_COMPLETED_AUCTION_MESSAGE.getMessage(), auction.getTitle()), auction)));
    }

    public void publishToSellerForCompletedAuction(Auction auction, Long bidMemberId) {
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedAuction(
                        String.format(COMPLETE_BID_AUCTION_MESSAGE.getMessage(), auction.getTitle()),
                        auction,
                        bidMemberId)));
    }

    public void publishToWonBidderForRequestPay(Auction auction, Long bidMemberId) {
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AuctionAlarmPayload.ofRequestPay(
                        String.format(REQUEST_PAY_AUCTION_MESSAGE.getMessage(), auction.getTitle()),
                        auction,
                        bidMemberId)));
    }

    public void publishToBiddersForCancelAuction(Auction auction, Long bidMemberId) {
        mqPublisher.publish(AlarmEvent.of(
                CANCEL_AUCTION.getRoutingKey(),
                AuctionAlarmPayload.ofCancelAuctionToBidders(
                        String.format(CANCEL_AUCTION_MESSAGE.getMessage(), auction.getTitle()), auction, bidMemberId)));
    }

    public void publishToSellerForCancelAuction(Auction auction) {
        mqPublisher.publish(AlarmEvent.of(
                CANCEL_AUCTION.getRoutingKey(),
                AuctionAlarmPayload.ofCancelAuctionToSeller(
                        String.format(CANCEL_AUCTION_MESSAGE.getMessage(), auction.getTitle()), auction)));
    }

    public void publishForCompletedPayAndRequestDelivery(BiddingHistory biddingHistory) {
        String auctionTitle = biddingHistory.getAuction().getTitle();
        // 낙찰자에게 전송
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedPayToWonBidder(
                        String.format(COMPLETED_PAY_MESSAGE.getMessage(), auctionTitle),
                        biddingHistory.getAuction(),
                        biddingHistory.getMemberId())));
        // 판매자에게 전송
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedPayAndRequestDeliveryToSeller(
                        String.format(
                                COMPLETED_PAY_AND_REQUEST_DELIVERY_MESSAGE.getMessage(),
                                auctionTitle,
                                biddingHistory.getMember().getNickname()),
                        biddingHistory.getAuction(),
                        biddingHistory.getMemberId())));
    }

    public void publishForNotPaid(BiddingHistory biddingHistory) {
        String auctionTitle = biddingHistory.getAuction().getTitle();
        // 판매자에게 전송
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofNotPaidToSeller(
                        String.format(
                                BUYER_NOT_PAID_MESSAGE.getMessage(),
                                biddingHistory.getMember().getNickname(),
                                auctionTitle),
                        biddingHistory.getAuction(),
                        biddingHistory.getMemberId())));
        // 낙찰자에게 전송
        mqPublisher.publish(AlarmEvent.of(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofNotPaidToWonBidder(
                        String.format(
                                NOT_PAID_MESSAGE.getMessage(),
                                auctionTitle),
                        biddingHistory.getAuction(),
                        biddingHistory.getMemberId())));
    }
}
