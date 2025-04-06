package com.pfe.sytemedeconge.Controller;

import java.time.LocalDate;

import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.sytemedeconge.Service.CongeService;
import com.pfe.sytemedeconge.Service.JwtUtil;

import Model.Conge;
import Model.Department;
import Model.Utilisateur;
import Repository.CongeRepository;

import Repository.UtilisateurRepository;

@RestController
@RequestMapping("/charts")
@CrossOrigin(origins = "http://localhost:4200")
public class ChartController {


    @Autowired
    private CongeRepository congeRepository;
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Statistiques pour l'admin (globales)
    @GetMapping("/admin")
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Répartition des types de congé
        stats.put("typesConge", congeRepository.countByTypeGroupBy());
        
        // Taux d'occupation par service
        stats.put("occupationParService", congeRepository.getOccupationRateByService());
        
        return stats;
    }

    @GetMapping("/chef")
    public ResponseEntity<?> getChefStats(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Utilisateur chef = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Chef non trouvé"));
            
            if (chef.getService() == null) {
                return ResponseEntity.badRequest().body("Aucun service attribué au chef");
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("typesCongeService", congeRepository.countByTypeAndServiceId(chef.getService().getId()));
            stats.put("demandesMensuelles", congeRepository.getMonthlyRequestsByService(chef.getService().getId()));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Accès refusé: " + e.getMessage());
        }
    }


    @GetMapping("/employe/{userId}")
    public ResponseEntity<?> getEmployeStats(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String token) {

        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Utilisateur currentUser = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (!currentUser.getId().equals(userId) && !currentUser.getRole().getName().equals("ADMIN")) {
                return ResponseEntity.status(403).body("Accès non autorisé");
            }

            Map<String, Object> stats = new HashMap<>();

            // Étape 1 : récupérer les congés approuvés
            List<Conge> congesApprouves = congeRepository.findByUtilisateurIdAndStatus(userId, "APPROUVE");
            List<Conge> congesRejetes = congeRepository.findByUtilisateurIdAndStatus(userId, "REJETE");
            List<Conge> congesEnAttente = congeRepository.findByUtilisateurIdAndStatus(userId, "EN_ATTENTE");

            // Étape 2 : créer un map pour agréger par type
            Map<String, Long> soldeMap = new HashMap<>();

            for (Conge conge : congesApprouves) {
                String type = mapTypeToFrontend(conge.getType());
                long jours = ChronoUnit.DAYS.between(conge.getDateDebut(), conge.getDateFin()) + 1;
                soldeMap.put(type, soldeMap.getOrDefault(type, 0L) + jours);
            }

            // Étape 3 : convertir en format { type, jours }
            List<Map<String, Object>> soldeConge = new ArrayList<>();
            for (Map.Entry<String, Long> entry : soldeMap.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("type", entry.getKey());
                item.put("jours", entry.getValue());
                soldeConge.add(item);
            }

            // Ajouter les stats des congés approuvés, rejetés et en attente
            Map<String, Long> congesStats = new HashMap<>();
            congesStats.put("Approuvés", (long) congesApprouves.size());
            congesStats.put("Rejetés", (long) congesRejetes.size());
            congesStats.put("En attente", (long) congesEnAttente.size());

            stats.put("soldeConge", soldeConge);
            stats.put("historique", congeRepository.findByUtilisateurId(userId));
            stats.put("congesStats", congesStats);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.status(403).body("Erreur: " + e.getMessage());
        }
    }

    private String mapTypeToFrontend(String type) {
        switch (type.toUpperCase()) {
            case "PAYE":
            case "ANNUEL":
            case "ANNUELLE":
                return "Annuelle";
            case "RTT":
                return "RTT";
            case "MALADIE":
            case "SICK":
                return "Maladie";
            case "SANS_SOLDE":  // Assurez-vous d'utiliser "SANS_SOLDE" dans le backend comme c'est le nom du type
                return "Sans Solde";
            default:
                return "Autre";
        }
    }
}











































