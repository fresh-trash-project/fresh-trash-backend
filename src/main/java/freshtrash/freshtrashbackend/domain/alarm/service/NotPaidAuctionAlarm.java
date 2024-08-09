package freshtrash.freshtrashbackend.domain.alarm.service;

import freshtrash.freshtrashbackend.domain.auction.entity.BiddingHistory;
import freshtrash.freshtrashbackend.domain.member.entity.Member;
import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.auction.service.AuctionService;
import freshtrash.freshtrashbackend.domain.auction.service.BiddingHistoryService;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import freshtrash.freshtrashbackend.domain.alarm.service.template.BiddingHistoryAlarmTemplate;
import freshtrash.freshtrashbackend.producer.AuctionProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class NotPaidAuctionAlarm extends BiddingHistoryAlarmTemplate {
    private final MemberService memberService;
    private final AuctionService auctionService;
    private final BiddingHistoryService biddingHistoryService;

    public NotPaidAuctionAlarm(
            AuctionProducer producer,
            MemberService memberService,
            AuctionService auctionService,
            BiddingHistoryService biddingHistoryService) {
        super(producer);
        this.memberService = memberService;
        this.auctionService = auctionService;
        this.biddingHistoryService = biddingHistoryService;
    }

    @Transactional
    @Override
    public void update(BiddingHistory biddingHistory) {
        log.debug("flagCount + 1");
        this.memberService.updateFlagCount(biddingHistory.getMemberId(), Member.USER_FLAG_LIMIT);
        log.debug("경매 취소 -> AuctionStatus = CANCEL");
        this.auctionService.cancelAuction(biddingHistory.getAuctionId());
        log.debug("입찰 내역 삭제");
        // 추후 다음 순위의 입찰자에게 기회를 주도록 프로세스를 추가할 수 있음
        biddingHistoryService.deleteBiddingHistory(biddingHistory.getId());
    }

    @Override
    public void publishEvent(BiddingHistory biddingHistory) {
        this.producer.publishForNotPaid(biddingHistory);
    }

    @Override
    public boolean supports(AlarmType alarmType) {
        return alarmType == AlarmType.NOT_PAY;
    }
}
