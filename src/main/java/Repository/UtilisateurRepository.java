package Repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Model.Department;

import Model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findById(Long id);
   
   
  
    Utilisateur findByServiceAndRole_Name(Department service, String roleName);
    
    List<Utilisateur> findByServiceId(Long serviceId);
    long countByServiceId(Long serviceId);
    @Query(value = "SELECT COUNT(*) FROM utilisateur", nativeQuery = true)
    long countTotalUsers();
}