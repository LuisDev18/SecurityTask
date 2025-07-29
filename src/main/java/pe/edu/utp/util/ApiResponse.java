package pe.edu.utp.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
  private int status;
  private String code;
  private String message;
  private LocalDateTime timestamp;
  private T data;
  private List<ApiError> errors;

 public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", message, LocalDateTime.now(), data, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok("Operation successful", data); // Mensaje por defecto
    }

    public static <T> ApiResponse<T> ok(String message) {
        return ok(message, null); // Mensaje sin datos
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), "SUCCESS", message, LocalDateTime.now(), data, null);
    }

    public static <T> ApiResponse<T> accepted(String message) {
        return new ApiResponse<>(HttpStatus.ACCEPTED.value(), "SUCCESS", message, LocalDateTime.now(), null, null);
    }

     public static ApiResponse<Void> noContent() { 
        return new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "SUCCESS", "No content", LocalDateTime.now(), null, null);
    }

    // --- Métodos de Ayuda para Respuestas de ERROR (como ya tenías) ---

    // Constructor/Método para errores generales (ej. manejado por GlobalExceptionHandler)
    public static <T> ApiResponse<T> error(HttpStatus httpStatus, String code, String message, String debugMessage) {
        // En producción, probablemente no incluyas debugMessage directamente aquí
        return new ApiResponse<>(httpStatus.value(), code, message + (debugMessage != null ? " [Debug: " + debugMessage + "]" : ""), LocalDateTime.now(), null, null);
    }

    // Constructor/Método para errores con lista de ApiError (ej. validación)
    public static <T> ApiResponse<T> error(HttpStatus httpStatus, String code, String message, List<ApiError> errors) {
        return new ApiResponse<>(httpStatus.value(), code, message, LocalDateTime.now(), null, errors);
    }

    // Método de ayuda para construir ResponseEntity
    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        // Si el status es NO_CONTENT, el body debe ser null en ResponseEntity
        if (this.status == HttpStatus.NO_CONTENT.value()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(this, HttpStatus.valueOf(this.status));
    }

}
