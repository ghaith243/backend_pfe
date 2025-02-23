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
        if (!type.equalsIgnoreCase("Maternité")) {
            return true; // Si ce n'est pas un congé maternité, aucune règle spécifique
        }

        int dureeMax = (enfantCount <= 2) ? 16 : 26; // Durée en semaines
        long dureeDemandeeEnJours = dateDebut.until(dateFin, ChronoUnit.DAYS);
        int dureeDemandeeEnSemaines = (int) (dureeDemandeeEnJours / 7); // Durée en semaines entière

        System.out.println("Durée demandée en jours : " + dureeDemandeeEnJours);
        System.out.println("Durée demandée en semaines : " + dureeDemandeeEnSemaines);
        
        return dureeDemandeeEnSemaines <= dureeMax;
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
