package com.pfe.sytemedeconge.Controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.sytemedeconge.Service.CongeService;
import com.pfe.sytemedeconge.Service.JwtUtil;
import com.pfe.sytemedeconge.Service.NotificationService;

import Model.Conge;
import Model.Service;
import Model.Utilisateur;
import Repository.CongeRepository;
import Repository.UtilisateurRepository;

@RestController
@RequestMapping("/conges")
@CrossOrigin(origins = "http://localhost:4200")  // Remplacez par votre URL frontend si nécessaire
public class CongeController {

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil; 
    @Autowired
    CongeService congeService;// Assurez-vous que vous avez un utilitaire pour extraire les informations du JWT
    @Autowired
    private NotificationService notificationService;
    // Endpoint pour soumettre une demande de congé
    @PostMapping("/demande")
    public ResponseEntity<?> createDemandeConge(
            @RequestBody Conge conge,
            @RequestHeader("Authorization") String token) {

        // Extraire l'email et le rôle de l'utilisateur à partir du JWT
        String email = jwtUtil.extractEmail(token.substring(7));
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier le chevauchement de congé
        if (!congeService.isChevauchementAutorise(
                utilisateur.getService().getId(),
                conge.getDateDebut(),
                conge.getDateFin(),
                utilisateur.getId())) { // Passer l'ID de l'utilisateur actuel
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Il y a déjà trop de congés dans cette période.");
        }

        

        
    

        
        // Vérifier la validité du congé maternité
        //System.out.println("Nombre d'enfants de l'utilisateur : " + utilisateur.getEnfantCount());
        if (!congeService.isCongeMaterniteValide(conge.getType(), utilisateur.getEnfantCount(), conge.getDateDebut(), conge.getDateFin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La durée du congé maternité dépasse la limite autorisée.");
        }

        // Lier l'utilisateur et le service à la demande de congé
        conge.setUtilisateur(utilisateur);
        conge.setService(utilisateur.getService());

        // Enregistrer la demande de congé
        congeRepository.save(conge);
        String notificationMessage = "Nouvelle demande de congé de " + utilisateur.getNom() + " (" + utilisateur.getEmail() + ")";
        notificationService.notifyChefAndAdmin(notificationMessage, utilisateur);


        return ResponseEntity.ok("Demande de congé soumise avec succès !");
     
       
    }

    // Endpoint pour valider ou rejeter une demande de congé
    @PutMapping("/{congeId}/status")
    public ResponseEntity<?> updateCongeStatus(
            @PathVariable Long congeId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {
        
        // Extraire l'email et le rôle de l'utilisateur à partir du JWT
        String email = jwtUtil.extractEmail(token.substring(7));
        String role = jwtUtil.extractRole(token.substring(7));

        // Récupérer l'utilisateur à partir de l'email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer la demande de congé à partir de son ID
        Conge conge = congeRepository.findById(congeId)
                .orElseThrow(() -> new RuntimeException("Demande de congé non trouvée"));

        // Vérifier si l'utilisateur a le droit de valider ou rejeter cette demande
        if ("EMPLOYE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à approuver/rejeter cette demande.");
        }

        // Si l'utilisateur est un CHEF, il peut valider ou rejeter une demande dans son propre service
        if ("CHEF".equals(role) && !utilisateur.getService().getId().equals(conge.getService().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à approuver/rejeter cette demande.");
        }

        // L'ADMIN peut valider ou rejeter toutes les demandes
        if ("ADMIN".equals(role)) {
            // Un admin peut valider ou rejeter n'importe quelle demande
        }

        // Récupérer le statut de la demande (accepté/rejeté)
        String status = request.get("status");

        // Vérifier que le statut est bien 'APPROUVE' ou 'REJETE' avant de mettre à jour
        if (!"APPROUVE".equals(status) && !"REJETE".equals(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le statut doit être 'APPROUVE' ou 'REJETE'.");
        }

        // Mettre à jour le statut de la demande de congé
        conge.setStatus(status);
        congeRepository.save(conge);
        String notificationMessage = "Votre demande de congé a été " + status.toLowerCase() + ".";
        notificationService.notifyUser(conge.getUtilisateur().getId(), notificationMessage);


        return ResponseEntity.ok("Statut mis à jour avec succès !");
    }

    // Endpoint pour récupérer les demandes de congé d'un utilisateur spécifique
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<?> getCongesByUtilisateur(@PathVariable Long utilisateurId) {
        Utilisateur user = new Utilisateur();
        user.setId(utilisateurId);
    	List<Conge> conges = congeService.getCongesByUtilisateur(user);
        return ResponseEntity.ok(conges);
    }

    // Endpoint pour récupérer les demandes de congé par service
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<?> getCongesByService(@PathVariable Long serviceId) {
    	Service service= new Service();
    	service.setId(serviceId);
        List<Conge> conges = congeService.getCongesByService(serviceId);
        return ResponseEntity.ok(conges);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllConge() {
        List<Conge> conges = congeRepository.findAll();
        return ResponseEntity.ok(conges);
    }
    
}
