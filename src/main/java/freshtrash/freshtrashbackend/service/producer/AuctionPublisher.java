package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AuctionAlarmPayload;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
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
        String message = String.format(NOT_COMPLETED_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(AUCTION_BID_COMPLETE.getRoutingKey(), AuctionAlarmPayload.ofNotExistBidders(message, auction));
    }

    public void publishToSellerForCompletedAuction(Auction auction, Long bidMemberId) {
        String message = String.format(COMPLETE_BID_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedAuction(message, auction, bidMemberId));
    }

    public void publishToWonBidderForRequestPay(Auction auction, Long bidMemberId) {
        String message = String.format(REQUEST_PAY_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(
                AUCTION_BID_COMPLETE.getRoutingKey(), AuctionAlarmPayload.ofRequestPay(message, auction, bidMemberId));
    }

    public void publishToBiddersForCancelAuction(Auction auction, Long bidMemberId) {
        String message = String.format(CANCEL_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(
                CANCEL_AUCTION.getRoutingKey(),
                AuctionAlarmPayload.ofCancelAuctionToBidders(message, auction, bidMemberId));
    }

    public void publishToSellerForCancelAuction(Auction auction) {
        String message = String.format(CANCEL_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(CANCEL_AUCTION.getRoutingKey(), AuctionAlarmPayload.ofCancelAuctionToSeller(message, auction));
    }

    public void publishForCompletedPayAndRequestDelivery(BiddingHistory biddingHistory) {
        String auctionTitle = biddingHistory.getAuction().getTitle();
        String bidderNickname = biddingHistory.getMember().getNickname();
        String bidderMessage = String.format(COMPLETED_PAY_MESSAGE.getMessage(), auctionTitle);
        String sellerMassage =
                String.format(COMPLETED_PAY_AND_REQUEST_DELIVERY_MESSAGE.getMessage(), auctionTitle, bidderNickname);
        // 낙찰자에게 전송
        publishAlarm(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedPayToWonBidder(
                        bidderMessage, biddingHistory.getAuction(), biddingHistory.getMemberId()));
        // 판매자에게 전송
        publishAlarm(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedPayAndRequestDeliveryToSeller(
                        sellerMassage, biddingHistory.getAuction(), biddingHistory.getMemberId()));
    }

    public void publishForNotPaid(BiddingHistory biddingHistory) {
        String auctionTitle = biddingHistory.getAuction().getTitle();
        String buyerNickname = biddingHistory.getMember().getNickname();
        String sellerMessage = String.format(BUYER_NOT_PAID_MESSAGE.getMessage(), buyerNickname, auctionTitle);
        String bidderMessage = String.format(NOT_PAID_MESSAGE.getMessage(), auctionTitle);
        // 판매자에게 전송
        publishAlarm(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofNotPaidToSeller(
                        sellerMessage, biddingHistory.getAuction(), biddingHistory.getMemberId()));
        // 낙찰자에게 전송
        publishAlarm(
                AUCTION_PAY.getRoutingKey(),
                AuctionAlarmPayload.ofNotPaidToWonBidder(
                        bidderMessage, biddingHistory.getAuction(), biddingHistory.getMemberId()));
    }

    public void publishToSellerForReview(Auction auction, Long buyerId) {
        publishAlarm(
                REVIEW.getRoutingKey(),
                AuctionAlarmPayload.ofReview(REVIEW_FROM_BUYER_MESSAGE.getMessage(), auction, buyerId));
    }

    private void publishAlarm(String routingKey, BaseAlarmPayload payload) {
        mqPublisher.publish(AlarmEvent.of(routingKey, payload));
    }
}
