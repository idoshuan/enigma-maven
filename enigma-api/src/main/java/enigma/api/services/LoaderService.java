package enigma.api.services;

import enigma.api.dtos.LoadResponse;
import enigma.core.Inventory;
import enigma.dal.converters.InventoryEntityConverter;
import enigma.dal.entities.MachineEntity;
import enigma.dal.repositories.MachineRepository;
import enigma.loader.xml.XMLLoader;
import enigma.sessions.MachineRegistry;
import enigma.sessions.exceptions.MachineAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class LoaderService {

    private final XMLLoader xmlLoader;
    private final MachineRegistry machineRegistry;
    private final MachineRepository machineRepository;
    private final InventoryEntityConverter converter;

    public LoaderService(XMLLoader xmlLoader, MachineRegistry machineRegistry,
                         MachineRepository machineRepository, InventoryEntityConverter converter) {
        this.xmlLoader = xmlLoader;
        this.machineRegistry = machineRegistry;
        this.machineRepository = machineRepository;
        this.converter = converter;
    }

    public LoadResponse loadMachine(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            Inventory inventory = xmlLoader.load(is);
            String name = inventory.name();

            if (machineRegistry.exists(name) || machineRepository.existsByName(name)) {
                throw new MachineAlreadyExistsException(name);
            }

            MachineEntity machineEntity = converter.toMachineEntity(inventory);
            machineRepository.save(machineEntity);

            machineRegistry.register(name, inventory);

            return LoadResponse.success(name);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file: " + e.getMessage());
        }
    }
}
