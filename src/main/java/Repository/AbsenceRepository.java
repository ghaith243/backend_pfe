package Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Model.Absence;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByEmployeId(Long employeId);
}

