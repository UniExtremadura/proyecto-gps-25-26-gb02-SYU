package com.gb02.syumsvc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
import com.gb02.syumsvc.utils.UsernameChecker;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


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

    /**
     * Applies changes from a payload to a user's base data.
     * Automatically hashes password if 'contrasena' field is present.
     * 
     * @param baseUser Original user data
     * @param changes Changes to apply
     * @return Modified user data
     */
    private Map<String, Object> applyUserChanges(Map<String, Object> baseUser, Map<String, Object> changes) {
        for (String key : changes.keySet()) {
            Object value = changes.get(key);
            if (key.equals("password")) {
                value = com.gb02.syumsvc.utils.SecureUtils.hashPassword((String) value);
            }
            if (key.equals("image")){
                String b64 = (String) value;
                String nick = changes.containsKey("username") ? (String) changes.get("username") : (String) baseUser.get("username");
                String extension = Base64Img.saveB64(b64, nick);
                value = "/pfp/" + nick + "." + extension;
            }
            if (key.equals("username")){
                if(!UsernameChecker.isValidUsername((String)changes.get("username"))){
                    throw new InvalidUsernameException();
                }
                if(baseUser.get("image") != null && !baseUser.get("image").toString().isBlank()){
                    String oldImagePath = (String) baseUser.get("image");
                    String newNick = (String) changes.get("username");
                    String newImagePath = Base64Img.changeNick(oldImagePath, newNick);
                    baseUser.put("image", newImagePath);
                }
            }
            baseUser.put(key, value);
        }
        return baseUser;
    }

    /**
     * Updates user information (partial update).
     * User can only modify their own data.
     * 
     * @param nick Username to update
     * @param payload Map containing fields to update
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with updated user data, or error message
     */
    @PatchMapping("/user/{nick}")
    public ResponseEntity<Map<String, Object>> patchUser(@PathVariable String nick, @RequestBody Map<String, Object> payload, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            UsuarioDTO requestedUser = Model.getModel().getUsuarioByNick(nick);
            int currentUserId = Model.getModel().getSessionByToken(sessionToken).getUserId();
            
            // Authorization check: user can only modify their own data
            if (requestedUser.getUserId() != currentUserId) {
                return ResponseEntity.status(403).body(Response.getErrorResponse(403, "You are not authorized to modify this user's data."));
            }
            
            // Apply changes and update user
            UsuarioDTO updatedUser = new UsuarioDTO();
            updatedUser.fromMap(applyUserChanges(requestedUser.toMap(), payload));

            Model.getModel().updateUsuario(currentUserId, updatedUser);
            updatedUser.setPassword(null);
            return ResponseEntity.ok().body(updatedUser.toMap());
        } catch (InvalidUsernameException e) {
            System.err.println("Invalid username during user update: " + e.getMessage());
            return ResponseEntity.status(400).body(Response.getErrorResponse(400, e.getMessage()));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found during user update: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired during user update: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (UserNotFoundException e) {
            System.err.println("User not found during update: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "User not found"));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error updating user: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while updating user data."));
        } catch (Exception e) {
            System.err.println("Unexpected error updating user: " + e.getMessage());
             
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while updating user data."));
        } 
    }

    /**
     * Deletes a user account.
     * User can only delete their own account.
     * 
     * @param nick Username to delete
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @DeleteMapping("/user/{nick}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String nick, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            UsuarioDTO requestedUser = Model.getModel().getUsuarioByNick(nick);
            int currentUserId = Model.getModel().getSessionByToken(sessionToken).getUserId();
            
            // Authorization check: user can only delete their own account
            if (requestedUser.getUserId() != currentUserId) {
                return ResponseEntity.status(403).body(Response.getErrorResponse(403, "You are not authorized to delete this user."));
            }
            String img = requestedUser.getImage();
            Model.getModel().deleteUsuario(requestedUser.getUserId());
            if (img != null && !img.isBlank()) {
                java.nio.file.Path path = java.nio.file.Paths.get("src/main/resources/static" + img);
                try {
                    java.nio.file.Files.deleteIfExists(path);
                } catch (java.io.IOException e) {
                    System.err.println("Failed to delete image file: " + e.getMessage());
                }
            }
            return ResponseEntity.ok().body(Response.getOnlyMessage("User deleted successfully."));
        } catch (UserNotFoundException e) {
            System.err.println("User not found during deletion: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "User not found"));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found during user deletion: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired during user deletion: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error deleting user: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while deleting user."));
        } catch (Exception e) {
            System.err.println("Unexpected error deleting user: " + e.getMessage());
             
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while deleting user."));
        } 
    }

    /**
     * Links the authenticated user to an external artist record.
     * Expects JSON body with: nombre, bio, fechainicio, email, socialmediaurl
     * Calls an external service (placeholder) which returns the artist id. If successful,
     * updates the current user's idArtista with the returned id and returns the updated user.
     */
    @PostMapping("/user/link-artist")
    public ResponseEntity<Map<String, Object>> linkArtist(@RequestBody Map<String, Object> payload, @CookieValue(value = "oversound_auth", required = true) String token) {
        Integer returnedArtistId = null;
        try {
            
            if(payload.get("artisticName") == null) {
                return ResponseEntity.status(400).body(Response.getErrorResponse(400, "Missing required field: artisticName."));
            }

            if(payload.get("artisticEmail") == null) {
                return ResponseEntity.status(400).body(Response.getErrorResponse(400, "Missing required field: artisticEmail."));
            }

            SesionDTO sesion = Model.getModel().getSessionByToken(token);
            int currentUserId = sesion.getUserId();
            UsuarioDTO user = Model.getModel().getUsuario(currentUserId);
            if (user.getRelatedArtist() != null && getArtist(user.getRelatedArtist()) != null) {
                return ResponseEntity.status(400).body(Response.getErrorResponse(400, "User is already linked to an artist."));
            }

            returnedArtistId = createArtist(payload, token);
            if (returnedArtistId == null) {
                return ResponseEntity.status(502).body(Response.getErrorResponse(502, "Failed to link artist: external service error."));
            }

            // Update user with new artist id
            user.setRelatedArtist(returnedArtistId);
            Model.getModel().updateUsuario(currentUserId, user);
            user.setPassword(null);

            return ResponseEntity.ok().body(user.toMap());
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found during link-artist: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired during link-artist: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (UserNotFoundException e) {
            if(returnedArtistId != null) deleteArtist(returnedArtistId, token);
            System.err.println("User not found during link-artist: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "User not found"));
        } catch (UnexpectedErrorException e) {
            if(returnedArtistId != null) deleteArtist(returnedArtistId, token);
            System.err.println("Unexpected error linking artist: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while linking artist."));
        } catch (Exception e) {
            if(returnedArtistId != null) deleteArtist(returnedArtistId, token);
            System.err.println("General error linking artist: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while linking artist."));
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

    private Integer createArtist(@RequestBody Map<String, Object> payload, String token) {
        try {
            String url = TYA_SERVER+"/artist/upload";
            
            // Configurar headers con cookie
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Cookie", "oversound_auth=" + token);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            // Hacer la request
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>)(ResponseEntity<?>)restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                return (Integer) body.get("artistId");
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error calling external service: " + e.getMessage());
            return null;
        }
    }

    private Integer deleteArtist(int id, String token){
        try {
            String url = TYA_SERVER+"/artist/"+id;
            
            // Configurar headers con cookie
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", "oversound_auth=" + token);
            
            HttpEntity<?> request = new HttpEntity<>(headers);
            restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, request, Void.class);
            return id;
        } catch (Exception e) {
            System.err.println("Error calling external service to delete artist: " + e.getMessage());
            return null;
        }
    }
    
}

