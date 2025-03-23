package com.pfe.sytemedeconge.Controller;

import Model.Notification;
import Model.Utilisateur;
import Repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pfe.sytemedeconge.Service.JwtUtil;
import com.pfe.sytemedeconge.Service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationservice;
    @Autowired
     private UtilisateurRepository utilisateurRepository;
    @Autowired
    private JwtUtil jwtUtil;
    

    @GetMapping
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Notification> notifications = notificationservice.getAllNotifications(utilisateur.getId());

        return ResponseEntity.ok(notifications);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupère toutes les notifications (lues et non lues)
        List<Notification> notifications = notificationservice.getAllNotifications(utilisateur.getId());

        return ResponseEntity.ok(notifications);
    }
    @PutMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        notificationservice.markNotificationsAsRead(utilisateur.getId());
        return ResponseEntity.ok().build();
    }
}