package enigma.dal.repositories;

import enigma.dal.entities.MachineRotorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MachineRotorRepository extends JpaRepository<MachineRotorEntity, UUID> {
    List<MachineRotorEntity> findByMachineId(UUID machineId);
}
