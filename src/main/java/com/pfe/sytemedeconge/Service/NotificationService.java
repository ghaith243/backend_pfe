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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Department service =  utilisateur.getService();

        if (service != null) {
            // Trouver le chef du service (supposons qu'il n'y ait qu'un seul chef par service)
            List<Utilisateur> destinataires = utilisateurRepository.findByServiceAndRole_Name(service, "CHEF");


            for (Utilisateur destinataire : destinataires) {
                LocalDateTime now = LocalDateTime.now();
                String formattedTime = now.format(formatter);

                // Prepare the message content
                String jsonMessage = String.format("{\"message\": \"%s\", \"createdAt\": \"%s\", \"read\": false}", message, formattedTime);

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


    public void notifyUser(Utilisateur destinataire, String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); 
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(formatter);

        String jsonMessage = String.format("{\"message\": \"%s\", \"createdAt\": \"%s\", \"read\": false}", 
                                           message, formattedTime);

        // ✅ Envoi WebSocket direct à l'utilisateur cible
        messagingTemplate.convertAndSend("/topic/user/" + destinataire.getId(), jsonMessage);

        // ✅ Enregistrement en base
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setCreatedAt(now);
        notification.setUtilisateur(destinataire);
        notification.setRead(false);
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