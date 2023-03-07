package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerValidationException(final ValidationException exception) {
        log.warn("409 {}", exception.getMessage());
        return new ErrorResponse("409", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final NotFoundException exception) {
        return new ErrorResponse("404", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerFailException(final FailException exception) {
        return new ErrorResponse("400", exception.getMessage());
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerUnsupportedStateException(final UnsupportedStateException exception) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}

