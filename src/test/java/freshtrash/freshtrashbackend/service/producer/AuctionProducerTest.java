package freshtrash.freshtrashbackend.service.producer;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.events.AlarmEvent;
import freshtrash.freshtrashbackend.dto.request.AuctionAlarmPayload;
import freshtrash.freshtrashbackend.dto.request.BaseAlarmPayload;
import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.service.producer.publisher.MQPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static freshtrash.freshtrashbackend.config.rabbitmq.QueueType.*;
import static freshtrash.freshtrashbackend.dto.constants.AlarmMessage.*;
import static org.assertj.core.api.Assertions.assertThatCode;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuctionProducerTest {
    @InjectMocks
    private AuctionProducer auctionProducer;

    @Mock
    private MQPublisher mqPublisher;

    @Test
    @DisplayName("판매자에게 입찰자가 없음을 알리는 메시지를 전송한다.")
    void publishToSellerForNotExistBidders() {
        // given
        Auction auction = Fixture.createAuction();
        String message = String.format(NOT_COMPLETED_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(AUCTION_BID_COMPLETE.getRoutingKey(), AuctionAlarmPayload.ofNotExistBidders(message, auction));
        // when
        assertThatCode(() -> auctionProducer.publishToSellerForNotExistBidders(auction))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("판매자에게 경매가 완료되었음을 알리는 메시지를 전송한다.")
    void publishToSellerForCompletedAuction() {
        // given
        Auction auction = Fixture.createAuction();
        Long bidMemberId = 2L;
        String message = String.format(COMPLETE_BID_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(
                AUCTION_BID_COMPLETE.getRoutingKey(),
                AuctionAlarmPayload.ofCompletedAuction(message, auction, bidMemberId));
        // when
        assertThatCode(() -> auctionProducer.publishToSellerForCompletedAuction(auction, bidMemberId))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("낙찰자에게 결제를 요청하는 메시지를 전송한다.")
    void publishToWonBidderForRequestPay() {
        // given
        Auction auction = Fixture.createAuction();
        Long bidMemberId = 2L;
        String message = String.format(REQUEST_PAY_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(
                AUCTION_BID_COMPLETE.getRoutingKey(), AuctionAlarmPayload.ofRequestPay(message, auction, bidMemberId));
        // when
        assertThatCode(() -> auctionProducer.publishToWonBidderForRequestPay(auction, bidMemberId))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("입찰자에게 경매가 취소되었음을 알리는 메시지를 전송한다.")
    void publishToBiddersForCancelAuction() {
        // given
        Auction auction = Fixture.createAuction();
        Long bidMemberId = 2L;
        String message = String.format(CANCEL_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(
                CANCEL_AUCTION.getRoutingKey(),
                AuctionAlarmPayload.ofCancelAuctionToBidders(message, auction, bidMemberId));
        // when
        assertThatCode(() -> auctionProducer.publishToBiddersForCancelAuction(auction, bidMemberId))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("판매자에게 경매가 취소되었음을 알리는 메시지를 전송한다.")
    void publishToSellerForCancelAuction() {
        // given
        Auction auction = Fixture.createAuction();
        String message = String.format(CANCEL_AUCTION_MESSAGE.getMessage(), auction.getTitle());
        publishAlarm(CANCEL_AUCTION.getRoutingKey(), AuctionAlarmPayload.ofCancelAuctionToSeller(message, auction));
        // when
        assertThatCode(() -> auctionProducer.publishToSellerForCancelAuction(auction))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("낙찰자와 판매자에게 결제가 완료되었음을 알리고 판매자에게는 배송요청에 대한 내용을 메시지에 추가하여 전송한다.")
    void publishForCompletedPayAndRequestDelivery() {
        // given
        BiddingHistory biddingHistory = Fixture.createBiddingHistoryWithAuctionAndMember(1L, 2L, 1000);
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
        // when
        assertThatCode(() -> auctionProducer.publishForCompletedPayAndRequestDelivery(biddingHistory))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("판매자와 낙찰자에게 결제되지 않았음을 알리는 메시지를 전송한다.")
    void publishForNotPaid() {
        // given
        BiddingHistory biddingHistory = Fixture.createBiddingHistoryWithAuctionAndMember(1L, 2L, 1000);
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
        // when
        assertThatCode(() -> auctionProducer.publishForNotPaid(biddingHistory)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("판매자에게 리뷰가 작성되었다는 메시지를 전송한다.")
    void publishToSellerForReview() {
        // given
        Auction auction = Fixture.createAuction();
        Long buyerId = 3L;
        publishAlarm(
                REVIEW.getRoutingKey(),
                AuctionAlarmPayload.ofReview(REVIEW_FROM_BUYER_MESSAGE.getMessage(), auction, buyerId));
        // when
        assertThatCode(() -> auctionProducer.publishToSellerForReview(auction, buyerId))
                .doesNotThrowAnyException();
        // then
    }

    private void publishAlarm(String routingKey, BaseAlarmPayload payload) {
        mqPublisher.publish(AlarmEvent.of(routingKey, payload));
    }
}