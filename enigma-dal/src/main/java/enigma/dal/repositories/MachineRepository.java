package enigma.dal.repositories;

import enigma.dal.entities.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineRepository extends JpaRepository<MachineEntity, UUID> {
    Optional<MachineEntity> findByName(String name);
    boolean existsByName(String name);
}
