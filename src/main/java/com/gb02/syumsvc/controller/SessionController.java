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

    /**
     * Authenticates a user and creates a new session.
     * Accepts either username or email as identifier.
     * 
     * @param payload Map containing 'username' (username or email) and 'password'
     * @return ResponseEntity with success message and session token, or error message
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, Object> payload) {
        try {
            // Validate required fields
            if (!payload.containsKey("username") || !payload.containsKey("password")) {
                return ResponseEntity.badRequest().body(Response.getErrorResponse(400, "Username and password are required."));
            }
            
            // Fetch user by username or email (auto-detect if contains @)
            UsuarioDTO usuario;
            if (payload.get("username").toString().contains("@")) {
                usuario = Model.getModel().getUsuarioByMail(payload.get("username").toString());
            } else {
                usuario = Model.getModel().getUsuarioByNick(payload.get("username").toString());
            }
            
            // Verify password
            Boolean passwordMatch = SecureUtils.verifyPassword(payload.get("password").toString(), usuario.getPassword());
            if (!passwordMatch) {
                return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Wrong user or password."));
            }
            
            // Create new session
            SesionDTO sesion = new SesionDTO();
            sesion.setUserId(usuario.getUserId());
            java.util.Date expDate = new java.util.Date();
            expDate.setTime(expDate.getTime() + SESSION_DURATION_MS);
            sesion.setExpirationDate(new Date(expDate.getTime()));
            sesion.setToken(SecureUtils.generateSessionToken());
            Model.getModel().insertarSesion(sesion);
            
            return ResponseEntity.ok().body(Map.of("message", "Login successful.", "session_token", sesion.getToken()));
        } catch (UserNotFoundException e) {
            System.err.println("User not found during login: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Wrong user or password."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during login."));
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
             
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during login."));
        }
    }

    /**
     * Validates a session token and returns the authenticated user's data.
     * 
     * @param token Session token from 'oversound_auth' cookie
     * @return ResponseEntity with user data (without password), or error message
     */
    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authenticateUser(@CookieValue(value = "oversound_auth", required = true) String token) {
        try {
            SesionDTO sesion = Model.getModel().getSessionByToken(token);
            if (sesion == null) {
                return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
            }
            
            UsuarioDTO usuario = Model.getModel().getUsuario(sesion.getUserId());
            usuario.setPassword(null); // Remove password from response
            return ResponseEntity.ok().body(usuario.toMap());
        } catch (SessionExpiredException e) {
            System.err.println("Session expired during authentication: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found during authentication: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error during authentication: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during authentication."));
        } catch (Exception e) {
            System.err.println("Unexpected error during authentication: " + e.getMessage());
             
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during authentication."));
        }
    }

    /**
     * Logs out a user by deleting their session.
     * 
     * @param token Session token from 'oversound_auth' cookie
     * @return ResponseEntity with success message, or error message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(@CookieValue(value = "oversound_auth", required = true) String token) {
        try {
            SesionDTO sesion = Model.getModel().getSessionByToken(token);
            if (sesion == null) {
                return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
            }
            
            Model.getModel().deleteSesion(sesion.getId());
            return ResponseEntity.ok().body(Response.getOnlyMessage("Logged out successfully"));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found during logout: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error during logout: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during logout."));
        } catch (Exception e) {
            System.err.println("Unexpected error during logout: " + e.getMessage());
             
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "An unknown error occurred during logout."));
        }
    }

}
