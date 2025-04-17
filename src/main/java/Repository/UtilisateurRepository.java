package Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Model.Department;

import Model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findById(Long id);



    List<Utilisateur> findByServiceAndRole_Name(Department service, String roleName);

    List<Utilisateur> findByServiceId(Long serviceId);
    long countByServiceId(Long serviceId);
    @Query(value = "SELECT COUNT(*) FROM utilisateur", nativeQuery = true)
    long countTotalUsers();

    @Query("SELECT u FROM Utilisateur u WHERE u.service.id = :serviceId AND u.role.name = 'CHEF'")
    List<Utilisateur> findChefsByServiceId(@Param("serviceId") Long serviceId);

    List<Utilisateur> findAllByRole_Name(String roleName);


    Collection<Utilisateur> findByIdIn(List<Long> ids);

    List<Utilisateur> findByEmailIn(List<String> emails);
}