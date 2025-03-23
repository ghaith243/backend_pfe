package Repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Model.Department;

@Repository

public interface ServiceRepository extends JpaRepository<Department,Long> {
	
	    Optional<Department> findById(Long id);
	}

