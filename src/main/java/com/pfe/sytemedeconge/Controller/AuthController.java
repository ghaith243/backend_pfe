package com.pfe.sytemedeconge.Controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.sytemedeconge.Service.EmailService;
import com.pfe.sytemedeconge.Service.JwtUtil;

import DTO.AuthRequest;
import DTO.AuthResponse;
import Model.Department;
import Model.Role;
 
import Model.Utilisateur;
import Repository.RoleRepository;
import Repository.ServiceRepository;  // Assurez-vous d'importer le repository de Service
import Repository.UtilisateurRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;  // Repository pour Role

    @Autowired
    private ServiceRepository serviceRepository;  // Repository pour Service

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;
    // Endpoint pour l'inscription
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest request) {
        // Vérification si l'email existe déjà
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà utilisé");
        }

        // Récupérer le rôle
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        // Récupérer un service à assigner à l'utilisateur (ici, on suppose que le service est envoyé avec la requête)
        Department service = serviceRepository.findById(request.getServiceId())  // Supposons que le service soit envoyé avec la requête
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));

        // Hachage du mot de passe
        String hashedPassword = BCrypt.hashpw(request.getMotDePasse(), BCrypt.gensalt());

        // Création de l'utilisateur et assignation des valeurs
        Utilisateur user = new Utilisateur();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setMotDePasse(hashedPassword);
        user.setRole(role);  // Défini par le frontend
        user.setService(service); // Assigner le service récupéré

      
      
   
        
        // Sauvegarde de l'utilisateur dans la base de données
        utilisateurRepository.save(user);

        return ResponseEntity.ok("Inscription réussie !");
    }

    // Endpoint pour la connexion
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!BCrypt.checkpw(request.getMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().getName(),user.getId()));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email non trouvé");
        }

        Utilisateur user = userOptional.get();
        String resetCode = String.format("%08d", new Random().nextInt(100_000_000));
        user.setResetCode(resetCode);
        user.setResetCodeExpiration(LocalDateTime.now().plusMinutes(15)); // valide 15 min
        utilisateurRepository.save(user);

        emailService.sendResetCode(email, resetCode);
        return ResponseEntity.ok("Code envoyé à votre adresse email.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
        @RequestParam String email,
        @RequestParam String code,
        @RequestParam String newPassword
    ) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        if (user.getResetCode() == null || !user.getResetCode().equals(code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code invalide");
        }

        if (user.getResetCodeExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code expiré");
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setMotDePasse(hashedPassword);
        user.setResetCode(null);
        user.setResetCodeExpiration(null);
        utilisateurRepository.save(user);

        return ResponseEntity.ok("Mot de passe mis à jour avec succès !");
    }

}
