package freshtrash.freshtrashbackend.domain.alarm.service.template;

import freshtrash.freshtrashbackend.domain.alarm.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.domain.alarm.service.parameter.AlarmTemplateParameter;

public interface AlarmTemplate {
    void sendAlarm(AlarmTemplateParameter param);

    boolean supports(AlarmType alarmType);
}
