package com.gb02.syumsvc.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gb02.syumsvc.exceptions.SessionExpiredException;
import com.gb02.syumsvc.exceptions.SessionNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.exceptions.UserNotFoundException;
import com.gb02.syumsvc.model.Model;
import com.gb02.syumsvc.model.dto.UsuarioDTO;
import com.gb02.syumsvc.utils.Response;


/**
 * REST controller for user management operations.
 * Handles CRUD operations on user resources.
 */
@RestController
public class UserController {

    private final String TYA_SERVER = "http://10.1.1.4:8081";
    private final RestTemplate restTemplate;
    
    public UserController() {
        // Configure RestTemplate with timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds connection timeout
        factory.setReadTimeout(10000);   // 10 seconds read timeout
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Retrieves user information by username (nick).
     * Returns public data for non-authenticated users, full data for the user themselves.
     * 
     * @param nick Username to retrieve
     * @param sessionToken Optional session token from 'oversound_auth' cookie
     * @return ResponseEntity with user data, or error message
     */
    @GetMapping("/user/{nick}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String nick, @CookieValue(value = "oversound_auth", required = false) String sessionToken) {
        try {
            UsuarioDTO requestedUser = Model.getModel().getUsuarioByNick(nick);
            
            int currentUserId = -1;
            // Attempt to get session only if token is present
            if (sessionToken != null && !sessionToken.isBlank()) {
                try {
                    currentUserId = Model.getModel().getSessionByToken(sessionToken).getUserId();
                } catch (SessionNotFoundException | SessionExpiredException e) {
                    currentUserId = -1; // Continue as unauthenticated
                } catch (Exception e) {
                    currentUserId = -1; // Invalid session, continue as unauthenticated
                }
            }
            
            // Hide private data if not the current user
            if (requestedUser.getUserId() != currentUserId) {
                requestedUser.setPassword(null);
                requestedUser.setFirstLastName(null);
                requestedUser.setSecondLastName(null);
            }
            
            return ResponseEntity.ok().body(requestedUser.toMap());
        } catch (UserNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "User not found"));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error fetching user: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching user data."));
        } catch (Exception e) {
            System.err.println("General error fetching user: " + e.getMessage());
             
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching user data."));
        } 
    }

    private Map<String, Object> getArtist(int id){
        try {
            String url = TYA_SERVER+"/artist/"+id;
            
            // Hacer la request
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>)(ResponseEntity<?>)restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error calling external service: " + e.getMessage());
            return null;
        }
    }
    
}

