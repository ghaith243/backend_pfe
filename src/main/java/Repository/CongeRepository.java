package Repository;


import java.time.LocalDate;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Model.Conge;
import Model.Department;
import Model.Utilisateur;
@Repository

public interface CongeRepository extends JpaRepository<Conge,Long>{
	@Query("SELECT c FROM Conge c " +
		       "WHERE c.utilisateur.service.id = :serviceId " +
		       "AND ((c.dateDebut BETWEEN :dateDebut AND :dateFin) " +
		       "OR (c.dateFin BETWEEN :dateDebut AND :dateFin) " +
		       "OR (c.dateDebut <= :dateDebut AND c.dateFin >= :dateFin))")
	
	
		public List<Conge> findOverlappingConges(Long serviceId, LocalDate dateDebut, LocalDate dateFin);
	  
	// Pour Admin
    @Query("SELECT c.type as type, COUNT(c) as count FROM Conge c GROUP BY c.type")
    List<Map<String, Object>> countByTypeGroupBy();
    
    @Query("SELECT s.nom as service, COUNT(c) as total, " +
           "SUM(CASE WHEN c.status = 'APPROVED' THEN 1 ELSE 0 END) as approves " +
           "FROM Conge c JOIN c.service s GROUP BY s.nom")
    List<Map<String, Object>> getOccupationRateByService();
    
    // Pour Chef de Service
    @Query("SELECT c.type as type, COUNT(c) as count FROM Conge c WHERE c.service.id = :serviceId GROUP BY c.type")
    List<Map<String, Object>> countByTypeAndServiceId(@Param("serviceId") Long serviceId);
    
    @Query("SELECT MONTH(c.dateDebut) as mois, COUNT(c) as count " +
           "FROM Conge c WHERE c.service.id = :serviceId AND YEAR(c.dateDebut) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(c.dateDebut)")
    List<Map<String, Object>> getMonthlyRequestsByService(@Param("serviceId") Long serviceId);
    
    // Pour Employé
    @Query("SELECT c.type as type, SUM(DATEDIFF(c.dateFin, c.dateDebut)) as jours " +
    	       "FROM Conge c WHERE c.utilisateur.id = :userId AND c.status = 'APPROVED' " +
    	       "GROUP BY c.type")
    	List<Map<String, Object>> getLeaveBalanceByUser(@Param("userId") Long userId);


    @Query("SELECT c FROM Conge c WHERE c.utilisateur.id = :userId ORDER BY c.dateDebut DESC")
    List<Conge> findByUtilisateurId(@Param("userId") Long userId);
	
	
	 List<Conge> findByServiceId(Long serviceId);
	  List<Conge> findByUtilisateur(Utilisateur utilisateur);
	 
	  List <Conge> findAll();
	  @Query("SELECT c FROM Conge c WHERE c.utilisateur.id = :userId AND c.status = :status")
	  List<Conge> findByUtilisateurIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

	// Pour Admin - Statistiques globales des statuts
	  @Query("SELECT 'APPROUVE' as status, COUNT(c) as count FROM Conge c WHERE c.status = 'APPROUVE' " +
	         "UNION SELECT 'REJETE' as status, COUNT(c) as count FROM Conge c WHERE c.status = 'REJETE' " +
	         "UNION SELECT 'EN_ATTENTE' as status, COUNT(c) as count FROM Conge c WHERE c.status = 'EN_ATTENTE'")
	  List<Map<String, Object>> getGlobalStatusStats();
	  
	 
	}

