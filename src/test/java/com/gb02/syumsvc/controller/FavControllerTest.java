package com.gb02.syumsvc.controller;

import com.gb02.syumsvc.model.Model;
import com.gb02.syumsvc.model.dto.UsuarioDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;

/**
 * Integration tests for FavController endpoints.
 * Tests favorites operations for songs, artists, and albums.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FavControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String testUsername = "tfav_" + (System.currentTimeMillis() % 100000000);
    private String testPassword = "testpassword123";
    private String testEmail = "tfav_" + (System.currentTimeMillis() % 100000000) + "@test.com";
    private Cookie authCookie;

    @BeforeEach
    public void setup() throws Exception {
        // Clean up test user if exists
        try {
            UsuarioDTO user = Model.getModel().getUsuarioByNick(testUsername);
            if (user != null) {
                Model.getModel().deleteUsuario(user.getUserId());
            }
        } catch (Exception e) {
            // User doesn't exist, that's fine
        }

        // Register and login to get auth token
        String registerJson = String.format("""
            {
                "username": "%s",
                "name": "Test",
                "firstLastName": "Fav",
                "email": "%s",
                "password": "%s"
            }
            """, testUsername, testEmail, testPassword);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = String.format("""
            {
                "username": "%s",
                "password": "%s"
            }
            """, testUsername, testPassword);

        MvcResult loginResult = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        // Extract token from response body and create cookie
        String responseBody = loginResult.getResponse().getContentAsString();
        String token = responseBody.split("session_token\":\"")[1].split("\"")[0];
        authCookie = new Cookie("oversound_auth", token);
    }

    // ==================== SONGS TESTS ====================

    @Test
    public void testGetFavSongs_EmptyList() throws Exception {
        mockMvc.perform(get("/favs/songs")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids").isEmpty());
    }

    @Test
    public void testFavSong_Success() throws Exception {
        int songId = 999;

        mockMvc.perform(post("/favs/songs/" + songId)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify song is in favorites
        mockMvc.perform(get("/favs/songs")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids[0]").value(songId));
    }

    @Test
    public void testFavSong_AlreadyExists() throws Exception {
        int songId = 998;

        // Add song to favorites
        mockMvc.perform(post("/favs/songs/" + songId)
                .cookie(authCookie))
                .andExpect(status().isOk());

        // Try to add again
        mockMvc.perform(post("/favs/songs/" + songId)
                .cookie(authCookie))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testUnfavSong_Success() throws Exception {
        int songId = 997;

        // Add song first
        mockMvc.perform(post("/favs/songs/" + songId)
                .cookie(authCookie))
                .andExpect(status().isOk());

        // Remove from favorites
        mockMvc.perform(delete("/favs/songs/" + songId)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify song is not in favorites
        mockMvc.perform(get("/favs/songs")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isEmpty());
    }

    @Test
    public void testUnfavSong_NotFound() throws Exception {
        int songId = 996;

        mockMvc.perform(delete("/favs/songs/" + songId)
                .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testFavSongs_Unauthorized() throws Exception {
        mockMvc.perform(get("/favs/songs"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== ARTISTS TESTS ====================

    @Test
    public void testGetFavArtists_EmptyList() throws Exception {
        mockMvc.perform(get("/favs/artists")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids").isEmpty());
    }

    @Test
    public void testFavArtist_Success() throws Exception {
        int artistId = 888;

        mockMvc.perform(post("/favs/artists/" + artistId)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify artist is in favorites
        mockMvc.perform(get("/favs/artists")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids[0]").value(artistId));
    }

    @Test
    public void testFavArtist_AlreadyExists() throws Exception {
        int artistId = 887;

        // Add artist to favorites
        mockMvc.perform(post("/favs/artists/" + artistId)
                .cookie(authCookie))
                .andExpect(status().isOk());

        // Try to add again
        mockMvc.perform(post("/favs/artists/" + artistId)
                .cookie(authCookie))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testUnfavArtist_Success() throws Exception {
        int artistId = 886;

        // Add artist first
        mockMvc.perform(post("/favs/artists/" + artistId)
                .cookie(authCookie))
                .andExpect(status().isOk());

        // Remove from favorites
        mockMvc.perform(delete("/favs/artists/" + artistId)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify artist is not in favorites
        mockMvc.perform(get("/favs/artists")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isEmpty());
    }

    @Test
    public void testUnfavArtist_NotFound() throws Exception {
        int artistId = 885;

        mockMvc.perform(delete("/favs/artists/" + artistId)
                .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== ALBUMS TESTS ====================

    @Test
    public void testGetFavAlbums_EmptyList() throws Exception {
        mockMvc.perform(get("/favs/albums")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids").isEmpty());
    }

    @Test
    public void testFavAlbum_Success() throws Exception {
        int albumId = 777;

        mockMvc.perform(post("/favs/albums/" + albumId)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify album is in favorites
        mockMvc.perform(get("/favs/albums")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isArray())
                .andExpect(jsonPath("$.ids[0]").value(albumId));
    }

    @Test
    public void testFavAlbum_AlreadyExists() throws Exception {
        int albumId = 776;

        // Add album to favorites
        mockMvc.perform(post("/favs/albums/" + albumId)
                .cookie(authCookie))
                .andExpect(status().isOk());

        // Try to add again
        mockMvc.perform(post("/favs/albums/" + albumId)
                .cookie(authCookie))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testUnfavAlbum_Success() throws Exception {
        int albumId = 775;

        // Add album first
        mockMvc.perform(post("/favs/albums/" + albumId)
                .cookie(authCookie))
                .andExpect(status().isOk());

        // Remove from favorites
        mockMvc.perform(delete("/favs/albums/" + albumId)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify album is not in favorites
        mockMvc.perform(get("/favs/albums")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids").isEmpty());
    }

    @Test
    public void testUnfavAlbum_NotFound() throws Exception {
        int albumId = 774;

        mockMvc.perform(delete("/favs/albums/" + albumId)
                .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== COMBINED TESTS ====================

    @Test
    public void testMultipleFavorites() throws Exception {
        // Add multiple songs
        mockMvc.perform(post("/favs/songs/100").cookie(authCookie)).andExpect(status().isOk());
        mockMvc.perform(post("/favs/songs/101").cookie(authCookie)).andExpect(status().isOk());
        mockMvc.perform(post("/favs/songs/102").cookie(authCookie)).andExpect(status().isOk());

        // Add multiple artists
        mockMvc.perform(post("/favs/artists/200").cookie(authCookie)).andExpect(status().isOk());
        mockMvc.perform(post("/favs/artists/201").cookie(authCookie)).andExpect(status().isOk());

        // Add multiple albums
        mockMvc.perform(post("/favs/albums/300").cookie(authCookie)).andExpect(status().isOk());

        // Verify all favorites
        mockMvc.perform(get("/favs/songs").cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(3));

        mockMvc.perform(get("/favs/artists").cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(2));

        mockMvc.perform(get("/favs/albums").cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids.length()").value(1));
    }
}
