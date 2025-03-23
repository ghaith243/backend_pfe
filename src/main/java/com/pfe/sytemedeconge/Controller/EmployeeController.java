package com.pfe.sytemedeconge.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pfe.sytemedeconge.Service.CustomUserDetailsService;
import com.pfe.sytemedeconge.Service.JwtUtil;

import DTO.UtilisateurDTO;
import Model.Utilisateur;
import Repository.UtilisateurRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;
    private final CustomUserDetailsService userService;

    public EmployeeController( CustomUserDetailsService userService) {
        this.userService = userService;
    }

    // Endpoint pour récupérer les données personnelles d'un employé
    @GetMapping("/me")
    public ResponseEntity<?> getEmployeeData(@RequestHeader("Authorization") String token) {
        // Extraire l'email depuis le token
        String email = jwtUtil.extractEmail(token.substring(7)); // Enlève "Bearer " du token

        // Trouver l'utilisateur par email
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Retourne uniquement les données personnelles de l'utilisateur
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO(
                user.getId(),
                user.getNom(),
                user.getEmail(),
                user.getRole().getName(),
                user.getService()
            );

            return ResponseEntity.ok(utilisateurDTO);
    }
    @PostMapping("/{userId}/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            userService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok("✅ Image uploadée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();  // Affiche l'erreur dans la console backend
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Erreur lors de l'upload : " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        byte[] image = userService.getProfilePicture(userId);
        if (image != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    @PutMapping("/{userId}/updateemployee")
    public ResponseEntity<Utilisateur> updateEmployee(@PathVariable long userId, 
                                                      @RequestBody Utilisateur updatedUser) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Mettre à jour les champs modifiables
        user.setEmail(updatedUser.getEmail());
        user.setNom(updatedUser.getNom());
        user.setEnfantCount(updatedUser.getEnfantCount());
        
       
        
        // Sauvegarder les modifications
        Utilisateur savedUser = utilisateurRepository.save(user);
        
        return ResponseEntity.ok(savedUser);
    }
    @GetMapping("/users")
    public ResponseEntity<List<Utilisateur>> getAllEmployees() {
        List<Utilisateur> users = utilisateurRepository.findAll();
        
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // Renvoie 204 No Content si la liste est vide
        }
        
        return ResponseEntity.ok(users);
    }


    
}
    
