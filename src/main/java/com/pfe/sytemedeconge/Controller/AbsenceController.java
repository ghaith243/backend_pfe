package com.pfe.sytemedeconge.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pfe.sytemedeconge.Service.AbsenceService;
import com.pfe.sytemedeconge.Service.CustomUserDetailsService;


import DTO.AbsenceRequest;
import Model.Absence;
import Model.Utilisateur;

@RestController
@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/api/absences")
public class AbsenceController {

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private CustomUserDetailsService utilisateurService;

    /**
     * Le chef marque un employé comme absent.
     */
    @PostMapping("/marquer")
    public ResponseEntity<Absence> marquerAbsence(@RequestBody AbsenceRequest request, Principal principal) {
        Utilisateur chef = utilisateurService.findByUsername(principal.getName());
        Utilisateur employe = utilisateurService.findById(request.getEmployeId());

        Absence absence = absenceService.marquerAbsence(
                employe,
                request.getMotif(),
                request.isJustifiee(),
                chef
        );

        return ResponseEntity.ok(absence);
    }

    /**
     * Liste des absences d’un employé par son ID.
     */
    @GetMapping("/employe/{id}")
    public ResponseEntity<List<Absence>> getAbsences(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getAbsencesByEmploye(id));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Absence>> getallAbsences() {
        return ResponseEntity.ok(absenceService.getAbsences());
    }
}
