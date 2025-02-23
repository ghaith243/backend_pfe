package Repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Model.Conge;
import Model.Service;
import Model.Utilisateur;
@Repository

public interface CongeRepository extends JpaRepository<Conge,Long>{
	@Query("SELECT c FROM Conge c " +
		       "WHERE c.utilisateur.service.id = :serviceId " +
		       "AND ((c.dateDebut BETWEEN :dateDebut AND :dateFin) " +
		       "OR (c.dateFin BETWEEN :dateDebut AND :dateFin) " +
		       "OR (c.dateDebut <= :dateDebut AND c.dateFin >= :dateFin))")
	
	
		public List<Conge> findOverlappingConges(Long serviceId, LocalDate dateDebut, LocalDate dateFin);
	  List<Conge> findByServiceId(Long serviceId);
	  List<Conge> findByUtilisateur(Utilisateur utilisateur);
	 
	  List <Conge> findAll();
	}

