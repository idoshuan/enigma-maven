# Enigma Machine - Exercise 3

## Git Repository

https://github.com/idoshuan/enigma-maven

## System Overview

The system implements an Enigma Machine simulator as a Spring Boot REST API.
Multiple machines can be loaded from XML files, operated concurrently via UUID-based sessions, and all data is persisted to a Postgres database.

The application is packaged as a Spring Boot uber JAR (`enigma-machine-server-ex3.jar`) and runs on `http://localhost:8080/enigma`.

---

## Module Structure

```
enigma-aggregator (root)
├── enigma-logic/              (aggregator for core logic modules)
│   ├── core                   (domain model)
│   ├── machine                (Enigma simulation engine)
│   ├── loader                 (XML loading + JAXB + validation)
│   ├── engine                 (orchestration layer)
│   └── sessions               (session & multi-machine management)
├── enigma-dal/                (JPA entities + Spring Data repositories)
├── enigma-api/                (REST controllers, services, DTOs)
└── enigma-app/                (Spring Boot main class + configuration)
```

---

## Main Classes and Their Roles

### enigma-logic/core

| Class | Role |
|-------|------|
| `Alphabet` / `AlphabetImpl` | Represents the machine's character set. Handles char-to-index and index-to-char conversions. |
| `Rotor` / `RotorImpl` | Single rotor with forward/backward wiring, notch position, and right-column character mapping. |
| `Reflector` / `ReflectorImpl` | Reflects signals back through the rotors. Stores symmetric wiring pairs. |
| `Inventory` | Record holding the full machine definition: name, alphabet, rotors map, reflectors map, required rotor count. |

### enigma-logic/machine

| Class | Role |
|-------|------|
| `Machine` / `MachineImpl` | The core Enigma simulation. Processes a character through: plugboard → forward rotors → reflector → backward rotors → plugboard. |
| `MountedRotor` | Wraps a `Rotor` with a mutable position. Handles rotation and notch-based cascading to the next rotor. |

### enigma-logic/loader

| Class | Role |
|-------|------|
| `XMLLoader` | Loads machine definitions from XML files (file path or `InputStream` for multipart upload). Validates against `Enigma-Ex3.xsd` using JAXB. |
| `XMLParser` | Converts JAXB-generated objects into domain model (`Inventory`). Builds rotor wiring maps and reflector pairs from XML positioning data. |
| `RomanNumeral` | Enum for converting between Roman numeral strings (I-V) and integer IDs (1-5) for reflectors. |

### enigma-logic/engine

| Class | Role |
|-------|------|
| `Engine` / `EngineImpl` | Business logic orchestrator. Handles manual/random configuration, message processing, statistics tracking, and configuration reset. Supports `loadFromInventory()` for session-based initialization. |
| `CodeValidator` | Validates machine code configurations (rotor IDs, positions, reflector ID, plugboard pairs) against the loaded inventory. |
| `CodeDetailsFactory` | Creates `CodeDetails` snapshots of the current machine state (rotor positions, notch distances, reflector, plugboard). |
| `StatisticsTracker` / `StatisticsTrackerImpl` | Tracks the number of processed messages per session. |

### enigma-logic/sessions

| Class | Role |
|-------|------|
| `MachineRegistry` | Holds loaded `Inventory` objects by machine name in a `ConcurrentHashMap`. Enforces unique machine names. |
| `SessionManager` | Manages UUID-based sessions. Each session (`SessionContext`) binds a session ID to a machine name and an isolated `Engine` instance. Uses `ConcurrentHashMap` for thread safety. |

### enigma-dal

| Class | Role |
|-------|------|
| `MachineEntity` | JPA entity for the `machines` table (id, name, rotors_count, abc). |
| `MachineRotorEntity` | JPA entity for the `machines_rotors` table (machine_id, rotor_id, notch, wiring_right, wiring_left). |
| `MachineReflectorEntity` | JPA entity for the `machines_reflectors` table (machine_id, reflector_id as Postgres enum, input, output). |
| `ProcessingEntity` | JPA entity for the `processing` table (machine_id, session_id, code, input, output, time). |
| `MachineRepository` | Spring Data repository for machines. Provides `findByName()` and `existsByName()`. |
| `ProcessingRepository` | Spring Data repository for processing history. Provides `findBySessionId()` and `findByMachineId()`. |
| `InventoryEntityConverter` | Converts between domain `Inventory` objects and JPA entities. Handles rotor wiring serialization and reflector pair encoding. |

### enigma-api — Controllers

| Class | Path | Role |
|-------|------|------|
| `LoaderController` | `/enigma/load` | Handles XML file upload (multipart). Delegates to `LoaderService`. |
| `SessionController` | `/enigma/session` | Creates and deletes sessions. Delegates to `SessionService`. |
| `ConfigurationController` | `/enigma/config` | Machine configuration: get status, manual setup, automatic setup, reset. Delegates to `ConfigurationService`. |
| `ProcessController` | `/enigma/process` | Encrypts/decrypts text through the Enigma machine. Delegates to `ProcessService`. |
| `HistoryController` | `/enigma/history` | Returns processing history by session or machine name. Delegates to `HistoryService`. |
| `GlobalExceptionHandler` | — | Maps domain exceptions to HTTP status codes (404, 409, 400). |

All controllers contain zero logic — they only extract request parameters and delegate to their corresponding service.

### enigma-api — Services

| Class | Role |
|-------|------|
| `LoaderService` | Parses uploaded XML, validates uniqueness, persists machine to DB, registers in `MachineRegistry`. |
| `SessionService` | Creates/deletes sessions via `SessionManager`. |
| `ConfigurationService` | Handles manual/automatic/reset configuration. Converts between API format (L→R rotors, Roman numeral reflectors, plug pairs) and engine format (R→L rotors, integer reflectors, flat plugboard string). |
| `ProcessService` | Processes text through the engine, measures duration, persists result to DB, returns output with current rotor positions. |
| `HistoryService` | Queries processing history from DB, groups entries by code configuration. Validates that exactly one of sessionID/machineName is provided. |

### enigma-api — DTOs

| Class | Role |
|-------|------|
| `LoadResponse` | Response for `/load`: `{success, name, error}`. Uses `@JsonInclude(NON_NULL)`. |
| `SessionResponse` | Response for session creation: `{sessionID}`. |
| `ConfigResponse` | Response for `/config`: counts + compact strings + optional verbose structures. |
| `EnigmaCodeStructure` | Verbose machine state: rotors (with notchDistance), reflector, plugs. |
| `ProcessResponse` | Response for `/process`: `{output, currentRotorsPositionCompact}`. |
| `HistoryEntry` | Single history entry: `{input, output, duration}`. |
| `ManualConfigRequest` | Request body for manual config: `{sessionID, rotors, reflector, plugs}`. |
| `CompactCodeFormatter` | Utility that formats `CodeDetails` into compact string representation (e.g. `<1,3><A(3),B(0)><I><A|F>`). |

### enigma-app

| Class | Role |
|-------|------|
| `EnigmaApplication` | Spring Boot main class. Scans `enigma` base package. Configures `@EntityScan` and `@EnableJpaRepositories` for cross-module component detection. |
| `EnigmaConfig` | `@Configuration` class that registers non-Spring beans: `MachineRegistry`, `SessionManager`, `CompactCodeFormatter`, `XMLLoader`, `InventoryEntityConverter`. |

---

## Design Choices

### Session Architecture
Each session gets its own isolated `Engine` instance, created via `loadFromInventory()`. This ensures complete isolation between concurrent sessions — even sessions using the same machine definition don't share any mutable state. `ConcurrentHashMap` is used in both `MachineRegistry` and `SessionManager` for thread safety.

### Rotor Order Convention
The API receives rotors in visual left-to-right order (as a user would see them). The engine internally stores them right-to-left (index 0 = rightmost rotor). The conversion happens at the API boundary in `ConfigurationService` — rotors are reversed when configuring and reversed back when displaying.

### Reflector ID Mapping
The API uses Roman numeral strings ("I" through "V") for reflector IDs, matching the XML schema. The engine uses integer IDs (1-5). The `RomanNumeral` enum handles bidirectional conversion.

### DB Persistence Strategy
- Machines, rotors, and reflectors are persisted on load (`POST /load`).
- Processing results are persisted on every `POST /process` call.
- History queries (`GET /history`) read from the DB, so they work even after a session is deleted.
- Rotor wiring is stored as the original right and left character columns from the XML.
- Reflector mappings are stored as comma-separated position pairs.
- The `reflector_id` column uses a Postgres custom enum type (`reflector_id_enum`), mapped via Hibernate's `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.

### Layer Separation
The system follows strict layer separation:
- **Controllers** — HTTP handling only, zero logic, pure delegation to services.
- **Services** — All business logic, format conversions, and orchestration.
- **Repositories** — Database access only, no direct DB access from services or controllers.

### Error Handling
A `GlobalExceptionHandler` (`@RestControllerAdvice`) maps domain exceptions to appropriate HTTP responses:
- `SessionNotFoundException` → 404
- `MachineNotFoundException` → 409
- `MachineAlreadyExistsException` / `LoaderException` → 400
- `EngineException` / `MachineNotConfiguredException` → 400

