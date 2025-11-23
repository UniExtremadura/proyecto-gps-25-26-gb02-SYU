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
 * Integration tests for UserController endpoints.
 * Tests user CRUD operations (get, update, delete).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String testUsername = "tusr_" + (System.currentTimeMillis() % 100000000);
    private String testPassword = "testpassword123";
    private String testEmail = "tusr_" + (System.currentTimeMillis() % 100000000) + "@test.com";
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
                "firstLastName": "User",
                "secondLastName": "Controller",
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

    @Test
    public void testGetUser_Success() throws Exception {
        mockMvc.perform(get("/user/" + testUsername)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUsername))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.firstLastName").value("User"));
    }

    @Test
    public void testGetUser_NotFound() throws Exception {
        mockMvc.perform(get("/user/fake_" + System.currentTimeMillis())
                .cookie(authCookie))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testGetUser_NotAuthorizedFields() throws Exception {
        mockMvc.perform(get("/user/" + testUsername))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstLastName").isEmpty())
                .andExpect(jsonPath("$.secondLastName").isEmpty())
                .andExpect(jsonPath("$.password").isEmpty());
    }

    @Test
    public void testPatchUser_Success() throws Exception {
        String updateJson = """
            {
                "name": "UpdatedName",
                "firstLastName": "UpdatedLastName",
                "email": "%s"
            }
            """.formatted(testEmail);

        mockMvc.perform(patch("/user/" + testUsername)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());

        // Verify update
        mockMvc.perform(get("/user/" + testUsername)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.firstLastName").value("UpdatedLastName"))
                .andExpect(jsonPath("$.email").value(testEmail));
    }

    @Test
    public void testPatchUser_Unauthorized_DifferentUser() throws Exception {
        // Create another user
        String otherUsername = "otsr_" + System.currentTimeMillis();
        String otherEmail = "other_" + System.currentTimeMillis() + "@test.com";
        
        String registerJson = String.format("""
            {
                "username": "%s",
                "name": "Other",
                "firstLastName": "User",
                "email": "%s",
                "password": "password123"
            }
            """, otherUsername, otherEmail);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());

        // Try to update other user's profile
        String updateJson = """
            {
                "name": "Hacked",
                "firstLastName": "Name"
            }
            """;

        mockMvc.perform(patch("/user/" + otherUsername)
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/user/" + testUsername)
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Verify user is deleted
        mockMvc.perform(get("/user/" + testUsername)
                .cookie(authCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_Unauthorized_DifferentUser() throws Exception {
        // Create another user
        String otherUsername = "ot_del_" + System.currentTimeMillis();
        String otherEmail = "ot_del_" + System.currentTimeMillis() + "@t.c";
        
        String registerJson = String.format("""
            {
                "username": "%s",
                "name": "Other",
                "firstLastName": "User",
                "email": "%s",
                "password": "password123"
            }
            """, otherUsername, otherEmail);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());

        // Try to delete other user
        mockMvc.perform(delete("/user/" + otherUsername)
                .cookie(authCookie))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }
}
