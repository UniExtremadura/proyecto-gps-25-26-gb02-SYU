package com.gb02.syumsvc.controller;

import java.sql.Date;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gb02.syumsvc.exceptions.DupedEmailException;
import com.gb02.syumsvc.exceptions.DupedUsernameException;
import com.gb02.syumsvc.exceptions.InvalidUsernameException;
import com.gb02.syumsvc.exceptions.SessionExpiredException;
import com.gb02.syumsvc.exceptions.SessionNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.exceptions.UserNotFoundException;
import com.gb02.syumsvc.model.Model;
import com.gb02.syumsvc.model.dto.SesionDTO;
import com.gb02.syumsvc.model.dto.UsuarioDTO;
import com.gb02.syumsvc.utils.Base64Img;
import com.gb02.syumsvc.utils.Response;
import com.gb02.syumsvc.utils.SecureUtils;
import com.gb02.syumsvc.utils.UsernameChecker;


/**
 * REST controller for session management (login, register, authentication, logout).
 * Handles user registration, authentication, and session lifecycle.
 */
@RestController
public class SessionController {

    private static final long SESSION_DURATION_MS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    /**
     * Registers a new user and creates an initial session.
     * 
     * @param payload Map containing user data (username, password, email, etc.)
     * @return ResponseEntity with registered user data and session token, or error message
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> payload) {
        try {
            Model model = Model.getModel();
            // Create user from payload
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.fromMap(payload);

            if(usuario.getUsername() == null || !UsernameChecker.isValidUsername(usuario.getUsername())){
                throw new InvalidUsernameException();
            }

            if(usuario.getImage() != null && !usuario.getImage().isEmpty()) {
                // Save profile image to filesystem
                String extension = Base64Img.saveB64(usuario.getImage(), usuario.getUsername());
                usuario.setImage("/pfp/" + usuario.getUsername() + "." + extension);
            }
            
            // Hash password and register user
            usuario.setPassword(SecureUtils.hashPassword(usuario.getPassword()));
            UsuarioDTO nuevoUsuario = model.registrarUsuario(usuario);
            
            // Create initial session for the new user
            SesionDTO sesion = new SesionDTO();
            sesion.setUserId(nuevoUsuario.getUserId());
            java.util.Date expDate = new java.util.Date();
            expDate.setTime(expDate.getTime() + SESSION_DURATION_MS);
            sesion.setExpirationDate(new Date(expDate.getTime()));
            sesion.setToken(SecureUtils.generateSessionToken());
            Model.getModel().insertarSesion(sesion);
            
            // Remove password from response for security
            nuevoUsuario.setPassword(null);
            
            return ResponseEntity.ok().body(Map.of("registered_user", nuevoUsuario.toMap(), "session_token", sesion.getToken()));
        } catch (InvalidUsernameException e) {
            System.err.println("Invalid username during registration: " + e.getMessage());
            return ResponseEntity.status(400).body(Response.getErrorResponse(400, e.getMessage()));
        } catch (DupedEmailException e) {
            System.err.println("Duped email during registration: " + e.getMessage());
            return ResponseEntity.badRequest().body(Response.getErrorResponse(400, "This email is already registered."));
        } catch (DupedUsernameException e) {
            System.err.println("Duped username during registration: " + e.getMessage());
            return ResponseEntity.status(409).body(Response.getErrorResponse(409, "This username is already registered."));
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during registration."));
        }
    }

}
