package Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Model.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
	 Optional<Role> findByName(String name);
}
