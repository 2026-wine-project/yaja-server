package com.gbsw.template.global.exception;

import com.gbsw.template.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.warn("CustomException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("ValidationException: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.MISSING_FIELDS.getMessage());
        return ResponseEntity
                .status(ErrorCode.MISSING_FIELDS.getStatus())
                .body(ApiResponse.fail(ErrorCode.MISSING_FIELDS, message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        log.warn("BindException: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.MISSING_FIELDS.getMessage());
        return ResponseEntity
                .status(ErrorCode.MISSING_FIELDS.getStatus())
                .body(ApiResponse.fail(ErrorCode.MISSING_FIELDS, message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("MissingParam: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.MISSING_FIELDS.getStatus())
                .body(ApiResponse.fail(ErrorCode.MISSING_FIELDS, e.getParameterName() + " 파라미터가 필요합니다."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("NotReadable: {}", e.getMessage());
        if (e.getCause() instanceof DateTimeParseException) {
            return ResponseEntity
                    .status(ErrorCode.INVALID_DATE_FORMAT.getStatus())
                    .body(ApiResponse.fail(ErrorCode.INVALID_DATE_FORMAT));
        }
        return ResponseEntity
                .status(ErrorCode.MISSING_FIELDS.getStatus())
                .body(ApiResponse.fail(ErrorCode.MISSING_FIELDS, "요청 본문을 읽을 수 없습니다."));
    }

    @ExceptionHandler({DateTimeParseException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> handleDateTimeParse(Exception e) {
        log.warn("DateTimeParse: {}", e.getMessage());
        if (e instanceof MethodArgumentTypeMismatchException tme
                && !(tme.getCause() instanceof DateTimeParseException)) {
            return ResponseEntity
                    .status(ErrorCode.MISSING_FIELDS.getStatus())
                    .body(ApiResponse.fail(ErrorCode.MISSING_FIELDS, tme.getName() + " 파라미터 형식이 올바르지 않습니다."));
        }
        return ResponseEntity
                .status(ErrorCode.INVALID_DATE_FORMAT.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_DATE_FORMAT));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
