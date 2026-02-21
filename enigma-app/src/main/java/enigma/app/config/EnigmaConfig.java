package enigma.app.config;

import enigma.api.utils.CompactCodeFormatter;
import enigma.dal.converters.InventoryEntityConverter;
import enigma.loader.xml.XMLLoader;
import enigma.sessions.MachineRegistry;
import enigma.sessions.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnigmaConfig {

    @Bean
    public MachineRegistry machineRegistry() {
        return new MachineRegistry();
    }

    @Bean
    public SessionManager sessionManager(MachineRegistry machineRegistry) {
        return new SessionManager(machineRegistry);
    }

    @Bean
    public CompactCodeFormatter compactCodeFormatter() {
        return new CompactCodeFormatter();
    }

    @Bean
    public XMLLoader xmlLoader() {
        return new XMLLoader();
    }

    @Bean
    public InventoryEntityConverter inventoryEntityConverter() {
        return new InventoryEntityConverter();
    }
}
