package com.pfe.sytemedeconge.Service;

import Model.Notification;
import Model.Utilisateur;
import Repository.NotificationRepository;
import Repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository; 

    // Envoyer une notification au chef et à l'admin
    public void notifyChefAndAdmin(String message, Utilisateur utilisateur) {
        // Envoyer via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications", message);

        // Enregistrer en base de données
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUtilisateur(utilisateur);
        notificationRepository.save(notification);
    }

    // Envoyer une notification à l'utilisateur
    public void notifyUser(Long userId, String message) {
        // Récupérer l'utilisateur depuis la base de données
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")); // Déclarer et initialiser la variable utilisateur

        // Envoyer via WebSocket
        messagingTemplate.convertAndSend("/topic/user/" + userId, message);

        // Enregistrer en base de données
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUtilisateur(utilisateur); // Associer l'utilisateur récupéré
        notificationRepository.save(notification);
    }
}