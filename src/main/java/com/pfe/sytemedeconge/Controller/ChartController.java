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
    public ResponseEntity<?> getAdminStats() {
        Map<String, Object> response = new HashMap<>();
        
        // Données existantes
        response.put("typesConge", congeRepository.countByTypeGroupBy());
        response.put("occupationParService", congeRepository.getOccupationRateByService());
        
        // Nouvelles données pour les cartes statistiques
        List<Map<String, Object>> statusStats = congeRepository.getGlobalStatusStats();
        Map<String, Integer> congesStats = new HashMap<>();
        
        for (Map<String, Object> stat : statusStats) {
            String status = (String) stat.get("status");
            Long count = (Long) stat.get("count");
            
            // Convertir les statuts pour correspondre au format attendu par le frontend
            if ("APPROUVE".equals(status)) {
                congesStats.put("Approuvés", count.intValue());
            } else if ("REJETE".equals(status)) {
                congesStats.put("Rejetés", count.intValue());
            } else if ("EN_ATTENTE".equals(status)) {
                congesStats.put("En attente", count.intValue());
            }
        }
        
        response.put("congesStats", congesStats);
        
        return ResponseEntity.ok(response);
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
            // 👉 Ajout du calcul des stats des congés par statut
            Long serviceId = chef.getService().getId();
            List<Conge> congesService = congeRepository.findByServiceId(serviceId);

            Map<String, Long> congesStats = new HashMap<>();
            congesStats.put("Approuvés", congesService.stream().filter(c -> "APPROUVE".equals(c.getStatus())).count());
            congesStats.put("Rejetés", congesService.stream().filter(c -> "REJETE".equals(c.getStatus())).count());
            congesStats.put("En attente", congesService.stream().filter(c -> "EN_ATTENTE".equals(c.getStatus())).count());

            stats.put("congesStats", congesStats);

            
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
        switch (type) {
            case "PAYE":
                return "Congés payés"; // Retourner "Congés payés" pour correspondre au frontend
            
            
                
            
            case "MALADIE":
                return "Maladie";
            case "SANS_SOLDE":
                return "Sans Solde";
            case "Maternité": 
                return "Maternité";
            default:
                return "Autre";
        }
    }
}











































