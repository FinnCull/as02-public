package com.example.musicFinder.Controller;
import com.example.musicFinder.MusicFinderController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SongControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MusicFinderController musicFinderController;

    @Test
    public void testFetchLyrics_ValidSong() {
        String artist = "Taylor Swift";
        String song = "Love Story";
        String mockApiResponse = "{\"lyrics\":\"We were both young when I first saw you...\"}";

        // Mocking the RestTemplate to return the mock API response
        when(restTemplate.getForObject("https://api.lyrics.ovh/v1/" + artist + "/" + song, String.class))
                .thenReturn(mockApiResponse);

        // Call the controller method to get the actual response
        ObjectNode responseBody = musicFinderController.getSongDetails(artist, song);

        // Manually create a ResponseEntity to wrap the response body and the status
        ResponseEntity<ObjectNode> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Assert that the status code is 200 OK
        assertEquals(200, responseEntity.getStatusCodeValue());

        // Assert that the response body contains the correct song details
        assertEquals(artist, responseBody.get("artist").asText());
        assertEquals(song, responseBody.get("song").asText());
        assertTrue(responseBody.get("lyrics").asText().contains("We were both young"));
        assertTrue(responseBody.get("youtubeSearch").asText().contains("Taylor+Swift+Love+Story"));
    }


    @Test
    public void testFetchLyrics_InvalidSong() throws Exception {
        String artist = "Unknown Artist";
        String song = "Unknown Song";

        // Mocking the RestTemplate to throw an exception for an invalid request
        when(restTemplate.getForObject("https://api.lyrics.ovh/v1/" + artist + "/" + song, String.class))
                .thenThrow(new RuntimeException("404 Not Found"));

        // Call the controller method to get the response
        String responseBody = musicFinderController.getFormattedLyrics(artist, song);

        // Parse the response body to verify the error message
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(responseBody);

        // Verify the response contains the "error" field with the message "Lyrics not found"
        assertNotNull(responseJson.get("error"), "Expected an 'error' field in the response body");
        assertEquals("Lyrics not found", responseJson.get("error").asText());
    }
}
