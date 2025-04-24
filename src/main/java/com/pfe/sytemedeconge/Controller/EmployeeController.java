package com.pfe.sytemedeconge.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import Model.Department;
import Model.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import DTO.AuthRequest;
import DTO.UtilisateurDTO;
import Model.Utilisateur;
import Repository.RoleRepository;
import Repository.ServiceRepository;
import Repository.UtilisateurRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ServiceRepository serviceRepository;
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
    // Endpoint pour l'inscription
    @PostMapping("/ajouteruser")
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
        user.setEnfantCount(request.getEnfantCount())    ;  
      
   
        
        // Sauvegarde de l'utilisateur dans la base de données
        utilisateurRepository.save(user);

        return ResponseEntity.ok("Inscription réussie !");
    }

    @PutMapping("/{userId}/updateemployee")
    public ResponseEntity<Utilisateur> updateEmployee(@PathVariable long userId, @RequestBody AuthRequest request) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Récupérer le rôle
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        // Récupérer le service
        Department service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));

        // Hachage du mot de passe (seulement si fourni)
        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(request.getMotDePasse(), BCrypt.gensalt());
            user.setMotDePasse(hashedPassword);
        }

        // Mettre à jour les champs
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setService(service);
        user.setEnfantCount(request.getEnfantCount())    ;  

        // Sauvegarder les modifications
        Utilisateur savedUser = utilisateurRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
    
    @DeleteMapping("/deleteuser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        utilisateurRepository.deleteById(userId);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    @GetMapping("/users")
    public ResponseEntity<List<Utilisateur>> getAllEmployees() {
        List<Utilisateur> users = utilisateurRepository.findAll();
        
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // Renvoie 204 No Content si la liste est vide
        }
        
        return ResponseEntity.ok(users);
    }
  

    @GetMapping("/users/available")
    public Map<String, String> getAllChefsAndEmployees(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("test1");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("test");
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            System.out.println(token + " " + email + " " + "te");

            Optional<Utilisateur> employeeOpt = utilisateurRepository.findByEmail(email);
            System.out.println(employeeOpt);
            if (employeeOpt.isPresent()) {
                // Fetch all users (chefs and employees), exclude the currently logged-in user
                Map<String, String> usersMap = utilisateurRepository.findAll()
                        .stream()
                        .filter(u -> !u.getEmail().equals(email)) // exclude self
                        .collect(Collectors.toMap(Utilisateur::getEmail, Utilisateur::getNom)); // Map email to name
                System.out.println(usersMap);

                return usersMap;  // Return map of email -> name
            }
        }

        return Collections.emptyMap();
    }










}