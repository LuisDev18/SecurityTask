package pe.edu.utp.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import pe.edu.utp.util.ApiError;
import pe.edu.utp.util.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
    MethodArgumentNotValidException ex,
    WebRequest request
  ) {
    // Extrae los errores de validación y los mapea a tu estructura ApiError
    List<ApiError> errors = ex
      .getBindingResult()
      .getFieldErrors()
      .stream()
      .map(fieldError ->
        new ApiError(
          fieldError.getCode() != null ? fieldError.getCode() : "VALIDATION_ERROR",
          fieldError.getField(),
          fieldError.getDefaultMessage()
        )
      )
      .collect(Collectors.toList());

    // Construye la ApiResponse de error para validación
    ApiResponse<Object> apiResponse = ApiResponse.error(
      HttpStatus.BAD_REQUEST, // HTTP Status 400
      "VALIDATION_ERROR",
      "Validation failed for one or more fields.",
      errors
    );
    return apiResponse.toResponseEntity();
  }

  @ExceptionHandler(NoDataFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleNoDataFoundException(
    NoDataFoundException ex,
    WebRequest request
  ) {
    String message = ex.getMessage();
    if (message == null || message.isEmpty()) {
      message = "The requested resource could not be found.";
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    Throwable exceptionToPrint = (ex.getCause() != null) ? ex.getCause() : ex;
    exceptionToPrint.printStackTrace(pw);
    String debugMessage =
      "Request URI: " +
      request.getDescription(false) +
      "\n" +
      "Exception Type: " +
      ex.getClass().getName() +
      "\n" +
      "Exception Message: " +
      ex.getMessage() +
      "\n" +
      (
        ex.getCause() != null
          ? "Caused by: " +
          ex.getCause().getClass().getName() +
          " - " +
          ex.getCause().getMessage() +
          "\n"
          : ""
      ) +
      "Stack Trace:\n" +
      sw.toString();

    // Usar ApiResponse.error()
    ApiResponse<Object> apiResponse = ApiResponse.error(
      HttpStatus.NOT_FOUND,
      "RESOURCE_NOT_FOUND",
      message,
      debugMessage
    );
    return apiResponse.toResponseEntity();
  }

  @ExceptionHandler(EmailAlreadyException.class)
  public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyException(
    EmailAlreadyException ex,
    WebRequest request
  ) {
    String message = ex.getMessage();
    if (message == null || message.isEmpty()) {
      message = "The email provided is already registered.";
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);
    String debugMessage =
      "Request URI: " +
      request.getDescription(false) +
      "\n" +
      "Exception Type: " +
      ex.getClass().getName() +
      "\n" +
      "Exception Message: " +
      ex.getMessage() +
      "\n" +
      "Stack Trace:\n" +
      sw.toString();

    // Usar ApiResponse.error()
    ApiResponse<Object> apiResponse = ApiResponse.error(
      HttpStatus.CONFLICT,
      "EMAIL_ALREADY_EXISTS",
      message,
      debugMessage
    );
    return apiResponse.toResponseEntity();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGeneralException(
    Exception ex,
    WebRequest request
  ) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ex.printStackTrace(pw);
    String debugMessage =
      "Unexpected error: " +
      ex.getClass().getName() +
      " - " +
      ex.getMessage() +
      "\nStack Trace: " +
      sw.toString();

    // Usar ApiResponse.error()
    ApiResponse<Object> apiResponse = ApiResponse.error(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "INTERNAL_SERVER_ERROR",
      "An unexpected error occurred. Please check logs.",
      debugMessage
    );
    return apiResponse.toResponseEntity();
  }
}
