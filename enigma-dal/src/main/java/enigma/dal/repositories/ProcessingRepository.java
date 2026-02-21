package enigma.dal.repositories;

import enigma.dal.entities.ProcessingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessingRepository extends JpaRepository<ProcessingEntity, UUID> {
    List<ProcessingEntity> findBySessionId(String sessionId);
    List<ProcessingEntity> findByMachineId(UUID machineId);
}
