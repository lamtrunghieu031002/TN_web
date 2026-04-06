package backend.ptit.service.serviceImpl;

import backend.ptit.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã OTP Khôi phục mật khẩu - PTIT System");
        message.setText("Chào bạn,\n\n" +
                "Mã OTP để khôi phục mật khẩu của bạn là: " + otp + "\n\n" +
                "Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n" +
                "Trân trọng,\nĐội ngũ Admin PTIT");

        emailSender.send(message);
    }
}
