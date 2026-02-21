# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build all modules
mvn clean install

# Build the fat JAR
mvn clean package
# Output: enigma-app/target/enigma-machine-server-ex3.jar

# Run the application (requires Postgres running)
java -jar enigma-app/target/enigma-machine-server-ex3.jar

# Start Postgres via Docker
docker run -d -p 5432:5432 theshultz/patmal-enigma-postgres:1.0

# Regenerate JAXB classes from XSD (normally automatic during build)
mvn generate-sources -pl enigma-logic/loader
```

## Architecture

This is a Java 21 Enigma Machine simulator converted to a Spring Boot 3.2 REST API, organized as a multi-module Maven project:

```
enigma-aggregator (root pom, Spring Boot BOM)
├── enigma-logic/          (sub-aggregator for core logic modules)
│   ├── core               (domain model: Alphabet, Rotor, Reflector, Inventory)
│   ├── machine            (Enigma simulation: plugboard → rotors → reflector → rotors → plugboard)
│   ├── loader             (XML/JAXB config loading + validation against Enigma-Ex3.xsd)
│   └── engine             (orchestration: configuration, processing, statistics)
├── enigma-sessions/       (multi-machine session management: MachineRegistry, SessionManager)
├── enigma-dal/            (JPA entities + Spring Data repositories for Postgres)
├── enigma-api/            (REST controllers, services, DTOs, CompactCodeFormatter)
└── enigma-app/            (Spring Boot main class + config, produces fat JAR)
```

**enigma-logic/core** — Domain model interfaces and implementations: `Alphabet`, `Rotor`, `Reflector`, `Inventory` record (now includes `name` field from Ex3). No dependencies.

**enigma-logic/machine** — Enigma simulation: `Machine` processes characters through plugboard → forward rotors → reflector → backward rotors → plugboard. `MountedRotor` wraps a `Rotor` with mutable position and handles rotation/notch cascading.

**enigma-logic/loader** — Loads XML machine definitions validated against `Enigma-Ex3.xsd`. JAXB classes are auto-generated into `enigma.loader.xml.generated`. Supports loading from both file path and `InputStream` (for multipart upload).

**enigma-logic/engine** — Business logic: wires `Machine` + `CodeValidator` + `StatisticsTracker`. Handles manual/random configuration and message processing. Supports `loadFromInventory()` for session-based initialization.

**enigma-sessions** — `MachineRegistry` holds loaded `Inventory` objects by name. `SessionManager` creates UUID-based sessions with isolated `Engine` instances.

**enigma-dal** — JPA entities (`MachineEntity`, `MachineRotorEntity`, `MachineReflectorEntity`, `ProcessingEntity`) and Spring Data repositories. `InventoryEntityConverter` converts between domain objects and DB entities.

**enigma-api** — 5 REST controllers (`LoaderController`, `SessionController`, `ConfigurationController`, `ProcessController`, `HistoryController`), 5 services, DTOs, and `GlobalExceptionHandler`.

**enigma-app** — Spring Boot entry point with `@SpringBootApplication(scanBasePackages = "enigma")` and `EnigmaConfig` for non-Spring beans.

## REST API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/enigma/load` | Upload XML machine definition (multipart) |
| POST | `/enigma/session` | Create session for a named machine |
| DELETE | `/enigma/session?sessionID=` | Delete a session |
| GET | `/enigma/config?sessionID=&verbose=` | Get machine configuration |
| PUT | `/enigma/config/manual` | Configure machine manually (JSON body) |
| PUT | `/enigma/config/automatic?sessionID=` | Configure machine randomly |
| PUT | `/enigma/config/reset?sessionID=` | Reset to initial configuration |
| POST | `/enigma/process?input=&sessionID=` | Process (encrypt/decrypt) text |
| GET | `/enigma/history?sessionID=` or `?machineName=` | Get processing history |

## Database

Postgres on `localhost:5432/enigma` (user: `postgres`, password: `enigma`). Schema managed by the Docker image. JPA uses `ddl-auto=validate`.

## Key Design Patterns

- **Interface/Impl separation** throughout — nearly every class has a corresponding interface
- **Java records** for all DTOs (`MachineCode`, `CodeDetails`, `EngineDetails`, `Inventory`) with defensive copying
- **Validator pattern** — `Validator.validate()` throws specific exceptions from a rich hierarchy (`LoaderException`, `EngineException`)
- **Session-based architecture** — UUID sessions with `ConcurrentHashMap` for thread safety

## Important Conventions

- API receives rotors in visual L→R order; engine stores them R→L (index 0 = rightmost). Reversed at API boundary.
- Reflector IDs are Roman numerals (I–V) in the API, converted to ints (1–5) for the engine via `RomanNumeral` enum
- Plugboard pairs sent as `[{plug1, plug2}]` in API, concatenated to flat string for engine
- Alphabet must have even length and no duplicate characters
- Sample XML configs for manual testing are in `local-test-files/` (gitignored)
