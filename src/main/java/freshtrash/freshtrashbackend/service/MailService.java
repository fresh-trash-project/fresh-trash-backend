package freshtrash.freshtrashbackend.service;

import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements MailServiceInterface{
    private final JavaMailSender mailSender;

    @Override
    public void sendMailWithCode(String email, String subject, String text) {
        sendMail(email, subject, text);
  }

  public void sendMail(String toMail, String subject, String text) {
      MimeMessage mimeMessage = mailSender.createMimeMessage();

    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(toMail);
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(text, true);
      mailSender.send(mimeMessage);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }



}
