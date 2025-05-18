package com.pfe.sytemedeconge.Service;

import Model.ChatMessage;
import Model.Utilisateur;
import Repository.UtilisateurRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
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

    public void sendEmail(ChatMessage chatMessage) {
        Optional<Utilisateur> senderUserOpt = utilisateurRepository.findByEmail(chatMessage.getSender());
        String senderName = senderUserOpt.map(Utilisateur::getNom).orElse("Unknown");

        String to = chatMessage.getRecipient();
        String subject = "Nouveau message de " + senderName;
        String body = "Bonjour,\n\nVous avez reçu un nouveau message de " + senderName + ":\n\n" +
                chatMessage.getContent() + "\n\n" +
                "Cordialement,\nL'équipe de gestion de congés.";

        System.out.println("📨 Preparing to send email...");
        System.out.println("➡️  From: ghaith.hammi@esen.tn");
        System.out.println("➡️  To: " + to);
        System.out.println("➡️  Subject: " + subject);
        System.out.println("➡️  Body:\n" + body);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ghaith.hammi@esen.tn");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to " + to);
            e.printStackTrace();
        }
    }
        
    }
    
