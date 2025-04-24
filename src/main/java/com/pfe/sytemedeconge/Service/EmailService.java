package com.pfe.sytemedeconge.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Envoie un email avec une pièce jointe.
     */
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            // Ajouter la pièce jointe
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            mailSender.send(message);

            System.out.println("✅ Email envoyé à : " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Échec de l'envoi de l'email à : " + to);
            e.printStackTrace();
        }
    }
       

            public void sendResetCode(String toEmail, String resetCode) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Code de réinitialisation du mot de passe");
                message.setText("Voici votre code de réinitialisation : " + resetCode);
                mailSender.send(message);
            }
        
    }
    
