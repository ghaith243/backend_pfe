package Repository;

import Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Récupère toutes les notifications d'un utilisateur
    List<Notification> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);
    
    // Récupère uniquement les notifications non lues d'un utilisateur
    List<Notification> findByUtilisateurIdAndIsReadFalse(Long utilisateurId);
}
