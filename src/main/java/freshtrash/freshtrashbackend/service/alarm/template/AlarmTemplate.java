package freshtrash.freshtrashbackend.service.alarm.template;

import freshtrash.freshtrashbackend.entity.constants.AlarmType;
import freshtrash.freshtrashbackend.service.alarm.parameter.AlarmTemplateParameter;

public interface AlarmTemplate {
    void sendAlarm(AlarmTemplateParameter param);
    boolean supports(AlarmType alarmType);
}
