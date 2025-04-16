package com.pfe.sytemedeconge.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Model.Conge;
import Model.Utilisateur;
import Repository.CongeRepository;

@Service
public class CongeService {

    @Autowired
    private CongeRepository congeRepository;

    public boolean isChevauchementAutorise(Long serviceId, LocalDate dateDebut, LocalDate dateFin, Long utilisateurId) {
        // Récupérer tous les congés dans la période donnée (sans filtre de statut)
        List<Conge> chevauchements = congeRepository.findOverlappingConges(serviceId, dateDebut, dateFin);

        // Compter le nombre d'employés distincts en congé dans cette période (exclure l'utilisateur actuel)
        long nombreEmployesEnConge = chevauchements.stream()
                .filter(c -> !c.getUtilisateur().getId().equals(utilisateurId)) // Exclure l'utilisateur actuel
                .map(Conge::getUtilisateur)
                .distinct() // Garder seulement les employés distincts
                .count(); // Compter le nombre d'employés distincts

        // Si le nombre d'employés en congé est supérieur ou égal à 2, ne pas autoriser le congé
        if (nombreEmployesEnConge >= 2) {
            System.out.println("Plus de deux employés du même service sont en congé dans cette période. Congé non autorisé.");
            return false;
        }

        // Sinon, autoriser le chevauchement
        return true;
    }


    public boolean isCongeMaterniteValide(String type, int enfantCount, LocalDate dateDebut, LocalDate dateFin) {
        // Ajout de logs pour débogage
        System.out.println("Type de congé reçu: " + type);
        System.out.println("Comparaison avec 'Maternité': " + type.equals("Maternité"));
        
        if (type == null || !type.equals("Maternité")) {
            return true;
        }

        int dureeMaxSemaines = (enfantCount <= 2) ? 16 : 26;
        long dureeDemandeeEnJours = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        long dureeMaxEnJours = dureeMaxSemaines * 7L;
        
        System.out.println("Validation congé maternité:");
        System.out.println("Durée demandée: " + dureeDemandeeEnJours + " jours");
        System.out.println("Durée maximale: " + dureeMaxEnJours + " jours");
        
        return dureeDemandeeEnJours <= dureeMaxEnJours;
    }

    public List<Conge> getCongesByUtilisateur(Utilisateur utilisateur) {
        return congeRepository.findByUtilisateur(utilisateur);
    }
    public List<Conge> getCongesByService(long serviceId) {
        return congeRepository.findByServiceId(serviceId);
    }
    
    public List<Conge> findAllConges(){
    	return congeRepository.findAll();
    	
    	
    }
    }
