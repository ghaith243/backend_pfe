package Repository;

import Model.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {

    // Custom query to find group chats where a user is a participant
    @Query("SELECT g FROM GroupChat g JOIN g.participants p WHERE p.email = :email")
    List<GroupChat> findByParticipantsEmail(String email);

    @Query("SELECT u.email FROM GroupChat gc JOIN gc.participants u WHERE gc.id = :groupId")
    List<String> findParticipantsByGroupId(@Param("groupId") Long groupId);
}
