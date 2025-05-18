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

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private Map<String, Instant> lastEmailSentMap = new ConcurrentHashMap<>();
    private static final Duration EMAIL_COOLDOWN = Duration.ofMinutes(15);
    
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
        System.out.println("📬 sendEmail() called with message from: " + chatMessage.getSender());

        Optional<Utilisateur> senderUserOpt = utilisateurRepository.findByEmail(chatMessage.getSender());
        String senderName = senderUserOpt.map(Utilisateur::getNom).orElse("Utilisateur inconnu");

        String to = chatMessage.getRecipient();
        Instant now = Instant.now();
        Instant lastSent = lastEmailSentMap.get(to);
        if (lastSent != null && Duration.between(lastSent, now).compareTo(EMAIL_COOLDOWN) < 0) {
            System.out.println("Skipping email to " + to + " to avoid spamming.");
            System.out.flush();
            return;
        }

        String subject = "Nouveau message reçu de " + senderName;

        String body = new StringBuilder()
                .append("Bonjour,\n\n")
                .append("Vous avez reçu un nouveau message de ").append(senderName).append(".\n\n")
                .append("Contenu du message:\n")
                .append("----------------------------\n")
                .append(chatMessage.getContent()).append("\n")
                .append("----------------------------\n\n")
                .append("Veuillez vous connecter à la plateforme ArabSoft pour répondre ou consulter vos messages.\n\n")
                .append("Cordialement,\n")
                .append("L'équipe ArabSoft")
                .toString();

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
            lastEmailSentMap.put(to, now);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to " + to);
            e.printStackTrace();
        }
    }
        
    }
    
