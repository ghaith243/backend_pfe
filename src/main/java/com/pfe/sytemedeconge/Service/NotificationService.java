package com.pfe.sytemedeconge.Service;

import Model.Department;
import Model.Notification;
import Model.Utilisateur;
import Repository.NotificationRepository;
import Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.util.List;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Department service =  utilisateur.getService();

        if (service != null) {
            // Trouver le chef du service (supposons qu'il n'y ait qu'un seul chef par service)
            List<Utilisateur> destinataires = utilisateurRepository.findByServiceAndRole_Name(service, "CHEF");


            for (Utilisateur destinataire : destinataires) {
                LocalDateTime now = LocalDateTime.now();
                String formattedTime = now.format(formatter);

                // Prepare the message content
                String jsonMessage = String.format("{\"message\": \"%s\", \"time\": \"%s\"}", message, formattedTime);

                // Notify the chef via WebSocket (assuming you are using messagingTemplate for WebSocket)
                messagingTemplate.convertAndSend("/topic/user/" + destinataire.getId(), jsonMessage);

                // Optionally, save the notification in the database for persistence
                Notification notification = new Notification();
                notification.setMessage(message);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setUtilisateur(destinataire); // Link notification to the chef
                notificationRepository.save(notification);
            }
        }
    }


    // Envoyer une notification à l'utilisateur
    public void notifyUser(Long userId, String message) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créer un objet JSON pour la notification
        String jsonMessage = String.format("{\"message\": \"%s\"}", message);
        System.out.println("🔔 Notification JSON envoyée : " + jsonMessage);

        // Envoyer via WebSocket
        messagingTemplate.convertAndSend("/topic/user/" + userId, jsonMessage);

        // Enregistrer en base de données
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUtilisateur(utilisateur);
        notificationRepository.save(notification);
    }
    public List<Notification> getAllNotifications(Long utilisateurId) {
        return notificationRepository.findByUtilisateurIdOrderByCreatedAtDesc(utilisateurId);
    }

    /**
     * Récupérer uniquement les notifications non lues d'un utilisateur
     */
    public List<Notification> getUnreadNotifications(Long utilisateurId) {
        return notificationRepository.findByUtilisateurIdAndIsReadFalse(utilisateurId);
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    public void markNotificationsAsRead(Long utilisateurId) {
        List<Notification> unreadNotifications = notificationRepository.findByUtilisateurIdAndIsReadFalse(utilisateurId);
        if (!unreadNotifications.isEmpty()) {
            unreadNotifications.forEach(notification -> notification.setRead(true));
            notificationRepository.saveAll(unreadNotifications); // Sauvegarde en masse pour optimiser
        }
    }
}

    /*@Transactional
    public void markNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurIdAndIsReadFalse(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }*/
