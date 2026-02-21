package enigma.dal.repositories;

import enigma.dal.entities.MachineReflectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MachineReflectorRepository extends JpaRepository<MachineReflectorEntity, UUID> {
    List<MachineReflectorEntity> findByMachineId(UUID machineId);
}
