package enigma.api.controllers;

import enigma.api.dtos.ErrorResponse;
import enigma.api.dtos.LoadResponse;
import enigma.engine.exceptions.EngineException;
import enigma.engine.exceptions.machine.MachineNotConfiguredException;
import enigma.loader.core.exceptions.LoaderException;
import enigma.sessions.exceptions.MachineAlreadyExistsException;
import enigma.sessions.exceptions.MachineNotFoundException;
import enigma.sessions.exceptions.SessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<LoadResponse> handleMissingFile(MissingServletRequestPartException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(LoadResponse.failure("File not provided"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MachineNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMachineNotFound(MachineNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MachineAlreadyExistsException.class)
    public ResponseEntity<LoadResponse> handleMachineAlreadyExists(MachineAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(LoadResponse.failure(e.getMessage()));
    }

    @ExceptionHandler(LoaderException.class)
    public ResponseEntity<LoadResponse> handleLoaderException(LoaderException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(LoadResponse.failure(e.getMessage()));
    }

    @ExceptionHandler(MachineNotConfiguredException.class)
    public ResponseEntity<ErrorResponse> handleMachineNotConfigured(MachineNotConfiguredException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(EngineException.class)
    public ResponseEntity<ErrorResponse> handleEngineException(EngineException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }
}
