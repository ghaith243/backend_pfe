package com.pfe.sytemedeconge.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Model.Absence;
import Model.Utilisateur;
import Repository.AbsenceRepository;
import com.pfe.sytemedeconge.Service.SanctionService;

@Service
public class AbsenceService {

    @Autowired
    private AbsenceRepository absenceRepository;
    
    @Autowired
    private SanctionService sanctionService;

    /**
     * Marque une absence pour un employé, créée par un chef.
     */
    public Absence marquerAbsence(Utilisateur employe, String motif, boolean justifiee, Utilisateur chef) {
        Absence absence = new Absence();
        absence.setDate(LocalDate.now());
        absence.setMotif(motif);
        absence.setJustifiee(justifiee);
        absence.setEmploye(employe);
        absence.setChef(chef);
        
        Absence savedAbsence = absenceRepository.save(absence);
        
        // Vérifier si l'employé dépasse 15 jours d'absence dans l'année
        checkAndSanctionIfNeeded(employe);
        
        return savedAbsence;
    }

    /**
     * Récupère toutes les absences d'un employé par son ID.
     */
    public List<Absence> getAbsencesByEmploye(Long employeId) {
        return absenceRepository.findByEmployeId(employeId);
    }
    
    public List<Absence> getAbsences() {
        return absenceRepository.findAll();
    }
    
    /**
     * Compte le nombre de jours d'absence pour un employé dans l'année courante.
     */
    public int countAbsencesInCurrentYear(Utilisateur employe) {
        int currentYear = LocalDate.now().getYear();
        
        return (int) absenceRepository.findByEmployeId(employe.getId()).stream()
            .filter(absence -> absence.getDate().getYear() == currentYear)
            .count();
    }
    
    /**
     * Vérifie si un employé dépasse le seuil de 15 jours d'absence et le sanctionne si nécessaire.
     */
    private void checkAndSanctionIfNeeded(Utilisateur employe) {
        int absenceCount = countAbsencesInCurrentYear(employe);
        
        // Si l'employé vient de dépasser 15 jours d'absence
        if (absenceCount == 4) {
            sanctionService.generateAndSendSanctionNotice(employe);
        }
    }
}