package api.board.advice;

import api.board.exception.FileImageException;
import api.board.exception.NotActiveException;
import api.board.exception.RecommendedException;
import api.board.object.dto.error.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class MyControllerAdvice {


    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ErrorDto error = ErrorDto.builder()
                .error("Refresh Token Expired")
                .message(e.getMessage())
                .date(date)
                .build();
        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String message = e.getMessage();
        ErrorDto error = ErrorDto.builder()
                .error("Entity Not Found" + message)
                .message(e.getMessage())
                .date(date)
                .build();
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(FileImageException.class)
    protected ResponseEntity<?> handleFileImageException(FileImageException e) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ErrorDto error = ErrorDto.builder()
                .error("File Image Exception")
                .message(e.getMessage())
                .date(date)
                .build();
        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(NotActiveException.class)
    protected ResponseEntity<?> handleNotActiveException(NotActiveException e) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ErrorDto error = ErrorDto.builder()
                .error("Not Active Exception")
                .message(e.getMessage())
                .date(date)
                .build();
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    protected ResponseEntity<?> handleDuplicateKeyException(DuplicateKeyException e) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ErrorDto error = ErrorDto.builder()
                .error("Duplicate Key Exception")
                .message(e.getMessage())
                .date(date)
                .build();
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(RecommendedException.class)
    protected ResponseEntity<?> handleRecommendedException(RecommendedException e) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ErrorDto error = ErrorDto.builder()
                .error("Recommended Exception")
                .message(e.getMessage())
                .date(date)
                .build();
        return ResponseEntity.status(404).body(error);
    }

}
