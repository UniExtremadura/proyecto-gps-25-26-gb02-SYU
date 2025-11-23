package com.gb02.syumsvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import com.gb02.syumsvc.utils.Response;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
                return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.getErrorResponse(404, "Endpoint not found."));
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
                return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Response.getErrorResponse(405, "The HTTP method used is not supported for this endpoint."));
        }

        @ExceptionHandler(ServletRequestBindingException.class)
        public ResponseEntity<Map<String, Object>> handleMissingCookie(ServletRequestBindingException e) {
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, "Required parameter missing or invalid."));
        }

        // Captura cuando falta el request body o está mal formateado
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException e) {
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, "Request body is missing or malformed."));
        }

        // Captura errores de validación con @Valid
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
                String message = "no further information";
                FieldError fieldError = e.getBindingResult().getFieldError();
                if(fieldError != null){
                        message = fieldError.getDefaultMessage();
                }
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, "Validation failed: " + message));
        }

        // Captura errores de tipo de argumento
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, "Invalid parameter type: " + e.getName()));
        }

        // Captura errores genéricos de bad request
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, e.getMessage()));
        }

        // Captura errores genéricos de unsupported media type
        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<Map<String, Object>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
                return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Response.getErrorResponse(415, "Unsupported media type."));
        }

        // Captura cuando falta un parámetro de query requerido
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<Map<String, Object>> handleMissingParameter(MissingServletRequestParameterException e) {
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, "Missing required parameter: " + e.getParameterName()));
        }

        // Captura cuando falta una variable de path
        @ExceptionHandler(MissingPathVariableException.class)
        public ResponseEntity<Map<String, Object>> handleMissingPathVariable(MissingPathVariableException e) {
                return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.getErrorResponse(500, "Missing path variable: " + e.getVariableName()));
        }

        // Captura cuando falta una parte multipart (archivo)
        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<Map<String, Object>> handleMissingFilePart(MissingServletRequestPartException e) {
                return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.getErrorResponse(400, "Missing required file part: " + e.getRequestPartName()));
        }

        // Captura cuando el tamaño de archivo excede el límite
        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
                return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Response.getErrorResponse(413, "File size exceeds maximum limit."));
        }

        // Captura cuando el Accept header no es soportado
        @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
        public ResponseEntity<Map<String, Object>> handleNotAcceptable(HttpMediaTypeNotAcceptableException e) {
                return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(Response.getErrorResponse(406, "Requested media type is not acceptable."));
        }

        // Captura errores al escribir la respuesta
        @ExceptionHandler(HttpMessageNotWritableException.class)
        public ResponseEntity<Map<String, Object>> handleMessageNotWritable(HttpMessageNotWritableException e) {
                return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.getErrorResponse(500, "Error writing response."));
        }

        // Captura ResponseStatusException (usado por código custom)
        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
                return ResponseEntity
                .status(e.getStatusCode())
                .body(Response.getErrorResponse(e.getStatusCode().value(), e.getReason() != null ? e.getReason() : "Request failed."));
        }

        // Captura cualquier excepción no manejada
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
                return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.getErrorResponse(500, "Internal server error."));
        }


}