package com.restaurant.User_service.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "Nhà hàng của Thuận", "UTF-8"));
            helper.setTo(to);
            helper.setSubject("Mã xác thực đặt lại mật khẩu");
            helper.setText("Mã xác thực của bạn là: " + verificationCode 
                    + "\n Mã này có hiệu lực trong 15 phút.");

            mailSender.send(message);
        } catch (Exception e) {
            // Nên log lỗi hoặc ném ra ngoại lệ tùy theo logic
            e.printStackTrace();
        }
    }
}
