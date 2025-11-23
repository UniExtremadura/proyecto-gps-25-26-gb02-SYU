package com.gb02.syumsvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gb02.syumsvc.exceptions.FavAlreadyExistsException;
import com.gb02.syumsvc.exceptions.FavNotFoundException;
import com.gb02.syumsvc.exceptions.SessionExpiredException;
import com.gb02.syumsvc.exceptions.SessionNotFoundException;
import com.gb02.syumsvc.exceptions.UnexpectedErrorException;
import com.gb02.syumsvc.model.Model;
import com.gb02.syumsvc.model.dto.AlbumFavDTO;
import com.gb02.syumsvc.model.dto.ArtistaFavDTO;
import com.gb02.syumsvc.model.dto.CancionFavDTO;
import com.gb02.syumsvc.model.dto.SesionDTO;
import com.gb02.syumsvc.utils.Response;

/**
 * REST controller for favorites management (songs, artists, albums).
 * Handles CRUD operations on user favorite resources.
 */
@RestController
public class FavController {

    // ============================================
    // SONGS FAVORITES
    // ============================================

    /**
     * Retrieves all favorite songs for the authenticated user.
     * 
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with list of song IDs, or error message
     */
    @GetMapping("/favs/songs")
    public ResponseEntity<Map<String, Object>> getFavSongs(@CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Get favorite songs
            CancionFavDTO[] favs = Model.getModel().getCancionesFavByUser(userId);
            
            // Extract song IDs
            List<Integer> songIds = new ArrayList<>();
            for (CancionFavDTO fav : favs) {
                songIds.add(fav.getIdCancion());
            }

            return ResponseEntity.ok().body(Map.of("ids", songIds));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while fetching favorite songs: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while fetching favorite songs: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error fetching favorite songs: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching favorite songs."));
        } catch (Exception e) {
            System.err.println("General error fetching favorite songs: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching favorite songs."));
        }
    }

    /**
     * Adds a song to the authenticated user's favorites.
     * 
     * @param songId Song ID to add to favorites
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @PostMapping("/favs/songs/{songId}")
    public ResponseEntity<Map<String, Object>> favSong(@PathVariable int songId, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Create and insert favorite
            CancionFavDTO fav = new CancionFavDTO();
            fav.setIdCancion(songId);
            fav.setIdUsuario(userId);
            Model.getModel().insertCancionFav(fav);

            return ResponseEntity.ok().body(Response.getOnlyMessage("Song added to favorites successfully."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while adding favorite song: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while adding favorite song: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (FavAlreadyExistsException e) {
            System.err.println("Favorite song already exists: " + e.getMessage());
            return ResponseEntity.status(409).body(Response.getErrorResponse(409, "Song is already in favorites."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error adding favorite song: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while adding favorite song."));
        } catch (Exception e) {
            System.err.println("General error adding favorite song: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while adding favorite song."));
        }
    }

    /**
     * Removes a song from the authenticated user's favorites.
     * 
     * @param songId Song ID to remove from favorites
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @DeleteMapping("/favs/songs/{songId}")
    public ResponseEntity<Map<String, Object>> unfavSong(@PathVariable int songId, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Delete favorite
            Model.getModel().deleteCancionFav(songId, userId);

            return ResponseEntity.ok().body(Response.getOnlyMessage("Song removed from favorites successfully."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while removing favorite song: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while removing favorite song: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (FavNotFoundException e) {
            System.err.println("Favorite song not found: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "Song is not in favorites."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error removing favorite song: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while removing favorite song."));
        } catch (Exception e) {
            System.err.println("General error removing favorite song: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while removing favorite song."));
        }
    }

    // ============================================
    // ARTISTS FAVORITES
    // ============================================

    /**
     * Retrieves all favorite artists for the authenticated user.
     * 
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with list of artist IDs, or error message
     */
    @GetMapping("/favs/artists")
    public ResponseEntity<Map<String, Object>> getFavArtists(@CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Get favorite artists
            ArtistaFavDTO[] favs = Model.getModel().getArtistasFavByUser(userId);
            
            // Extract artist IDs
            List<Integer> artistIds = new ArrayList<>();
            for (ArtistaFavDTO fav : favs) {
                artistIds.add(fav.getIdArtista());
            }

            return ResponseEntity.ok().body(Map.of("ids", artistIds));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while fetching favorite artists: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while fetching favorite artists: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error fetching favorite artists: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching favorite artists."));
        } catch (Exception e) {
            System.err.println("General error fetching favorite artists: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching favorite artists."));
        }
    }

    /**
     * Adds an artist to the authenticated user's favorites.
     * 
     * @param artistId Artist ID to add to favorites
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @PostMapping("/favs/artists/{artistId}")
    public ResponseEntity<Map<String, Object>> favArtist(@PathVariable int artistId, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Create and insert favorite
            ArtistaFavDTO fav = new ArtistaFavDTO();
            fav.setIdArtista(artistId);
            fav.setIdUsuario(userId);
            Model.getModel().insertArtistaFav(fav);

            return ResponseEntity.ok().body(Response.getOnlyMessage("Artist added to favorites successfully."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while adding favorite artist: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while adding favorite artist: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (FavAlreadyExistsException e) {
            System.err.println("Favorite artist already exists: " + e.getMessage());
            return ResponseEntity.status(409).body(Response.getErrorResponse(409, "Artist is already in favorites."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error adding favorite artist: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while adding favorite artist."));
        } catch (Exception e) {
            System.err.println("General error adding favorite artist: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while adding favorite artist."));
        }
    }

    /**
     * Removes an artist from the authenticated user's favorites.
     * 
     * @param artistId Artist ID to remove from favorites
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @DeleteMapping("/favs/artists/{artistId}")
    public ResponseEntity<Map<String, Object>> unfavArtist(@PathVariable int artistId, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Delete favorite
            Model.getModel().deleteArtistaFav(artistId, userId);

            return ResponseEntity.ok().body(Response.getOnlyMessage("Artist removed from favorites successfully."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while removing favorite artist: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while removing favorite artist: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (FavNotFoundException e) {
            System.err.println("Favorite artist not found: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "Artist is not in favorites."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error removing favorite artist: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while removing favorite artist."));
        } catch (Exception e) {
            System.err.println("General error removing favorite artist: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while removing favorite artist."));
        }
    }

    // ============================================
    // ALBUMS FAVORITES
    // ============================================

    /**
     * Retrieves all favorite albums for the authenticated user.
     * 
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with list of album IDs, or error message
     */
    @GetMapping("/favs/albums")
    public ResponseEntity<Map<String, Object>> getFavAlbums(@CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Get favorite albums
            AlbumFavDTO[] favs = Model.getModel().getAlbumesFavByUser(userId);
            
            // Extract album IDs
            List<Integer> albumIds = new ArrayList<>();
            for (AlbumFavDTO fav : favs) {
                albumIds.add(fav.getIdAlbum());
            }

            return ResponseEntity.ok().body(Map.of("ids", albumIds));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while fetching favorite albums: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while fetching favorite albums: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error fetching favorite albums: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching favorite albums."));
        } catch (Exception e) {
            System.err.println("General error fetching favorite albums: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while fetching favorite albums."));
        }
    }

    /**
     * Adds an album to the authenticated user's favorites.
     * 
     * @param albumId Album ID to add to favorites
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @PostMapping("/favs/albums/{albumId}")
    public ResponseEntity<Map<String, Object>> favAlbum(@PathVariable int albumId, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Create and insert favorite
            AlbumFavDTO fav = new AlbumFavDTO();
            fav.setIdAlbum(albumId);
            fav.setIdUsuario(userId);
            Model.getModel().insertAlbumFav(fav);

            return ResponseEntity.ok().body(Response.getOnlyMessage("Album added to favorites successfully."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while adding favorite album: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while adding favorite album: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (FavAlreadyExistsException e) {
            System.err.println("Favorite album already exists: " + e.getMessage());
            return ResponseEntity.status(409).body(Response.getErrorResponse(409, "Album is already in favorites."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error adding favorite album: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while adding favorite album."));
        } catch (Exception e) {
            System.err.println("General error adding favorite album: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while adding favorite album."));
        }
    }

    /**
     * Removes an album from the authenticated user's favorites.
     * 
     * @param albumId Album ID to remove from favorites
     * @param sessionToken Session token from 'oversound_auth' cookie (required)
     * @return ResponseEntity with success message, or error message
     */
    @DeleteMapping("/favs/albums/{albumId}")
    public ResponseEntity<Map<String, Object>> unfavAlbum(@PathVariable int albumId, @CookieValue(value = "oversound_auth", required = true) String sessionToken) {
        try {
            // Get user ID from session
            SesionDTO sesion = Model.getModel().getSessionByToken(sessionToken);
            int userId = sesion.getUserId();

            // Delete favorite
            Model.getModel().deleteAlbumFav(albumId, userId);

            return ResponseEntity.ok().body(Response.getOnlyMessage("Album removed from favorites successfully."));
        } catch (SessionNotFoundException e) {
            System.err.println("Session not found while removing favorite album: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Invalid session token."));
        } catch (SessionExpiredException e) {
            System.err.println("Session expired while removing favorite album: " + e.getMessage());
            return ResponseEntity.status(401).body(Response.getErrorResponse(401, "Session has expired."));
        } catch (FavNotFoundException e) {
            System.err.println("Favorite album not found: " + e.getMessage());
            return ResponseEntity.status(404).body(Response.getErrorResponse(404, "Album is not in favorites."));
        } catch (UnexpectedErrorException e) {
            System.err.println("Unexpected error removing favorite album: " + e.getMessage());
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while removing favorite album."));
        } catch (Exception e) {
            System.err.println("General error removing favorite album: " + e.getMessage());
            
            return ResponseEntity.status(500).body(Response.getErrorResponse(500, "Unexpected error occurred while removing favorite album."));
        }
    }
}
