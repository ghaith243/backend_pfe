package Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Model.Service;
@Repository

public interface ServiceRepository extends JpaRepository<Service,Long> {
	
	    Optional<Service> findById(Long id);
	}

