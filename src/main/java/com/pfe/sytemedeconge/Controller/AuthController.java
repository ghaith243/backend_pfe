package com.pfe.sytemedeconge.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        user.setEnfantCount(request.getEnfantCount());
      
   
        
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
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().getName()));
    }
}
