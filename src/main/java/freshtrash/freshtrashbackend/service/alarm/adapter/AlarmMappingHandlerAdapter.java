package freshtrash.freshtrashbackend.service.alarm.adapter;

import freshtrash.freshtrashbackend.entity.Auction;
import freshtrash.freshtrashbackend.entity.BiddingHistory;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.exception.AlarmException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.service.alarm.parameter.AuctionAlarmParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.BiddingHistoryAlarmParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.ChatAlarmParameter;
import freshtrash.freshtrashbackend.service.alarm.parameter.ProductAlarmParameter;
import freshtrash.freshtrashbackend.service.alarm.template.AlarmTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AlarmMappingHandlerAdapter {
    private List<AlarmTemplate> handlerMappings;

    public AlarmMappingHandlerAdapter(ApplicationContext context) {
        this.initHandlerMappings(context);
    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = null;
        Map<String, AlarmTemplate> alarmTemplates =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context, AlarmTemplate.class, false, false);
        if (!alarmTemplates.isEmpty()) {
            this.handlerMappings = alarmTemplates.values().stream().toList();
        }
    }

    /**
     * 채팅 알림(채팅 상대방 신고)
     */
    public void handle(ChatRoom chatRoom, Long memberId, AlarmType alarmType) {
        AlarmTemplate handler = this.getHandler(alarmType);
        handler.sendAlarm(new ChatAlarmParameter(chatRoom, memberId));
    }

    /**
     * 경매 알림(경매 취소, 낙찰)
     */
    public void handle(Auction auction, AlarmType alarmType) {
        AlarmTemplate handler = this.getHandler(alarmType);
        handler.sendAlarm(new AuctionAlarmParameter(auction));
    }

    /**
     * 입찰 알림(미결제, 결재 완료)
     */
    public void handle(BiddingHistory biddingHistory, AlarmType alarmType) {
        AlarmTemplate handler = this.getHandler(alarmType);
        handler.sendAlarm(new BiddingHistoryAlarmParameter(biddingHistory));
    }

    /**
     * 중고 거래 알림(거래 완료, 거래 상태 변경)
     */
    public void handle(Long chatRoomId, Long memberId, AlarmType alarmType) {
        AlarmTemplate handler = this.getHandler(alarmType);
        handler.sendAlarm(new ProductAlarmParameter(chatRoomId, memberId));
    }

    private AlarmTemplate getHandler(AlarmType alarmType) {
        return this.handlerMappings.stream()
                .filter((alarmTemplate -> alarmTemplate.supports(alarmType)))
                .findFirst()
                .orElseThrow(() -> new AlarmException(ErrorCode.NOT_FOUND_ALARM_TEMPLATE));
    }
}
