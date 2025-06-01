package com.ushort.urlshortener;

import com.ushort.urlshortener.controllers.UrlShortenerController;
import com.ushort.urlshortener.entity.UrlMapping;
import com.ushort.urlshortener.dto.UpdateUrlRequest;
import com.ushort.urlshortener.dto.UrlRequest;
import com.ushort.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerApplicationTests {

    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    void testShortenUrl_Success() {
        String originalUrl = "http://example.com";
        UrlMapping urlMapping = new UrlMapping("short123", originalUrl);

        when(urlShortenerService.shortenUrl(originalUrl)).thenReturn(urlMapping);

        UrlRequest urlRequest = new UrlRequest();
        urlRequest.setOriginalUrl(originalUrl);
        ResponseEntity<String> response = urlShortenerController.shortenUrl(urlRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("short123"));
    }

    @Test
    void testShortenUrl_InvalidUrl() {
        String originalUrl = "invalid-url";
        UrlRequest urlRequest = new UrlRequest();
        urlRequest.setOriginalUrl(originalUrl);

        when(urlShortenerService.shortenUrl(originalUrl)).thenReturn(null);

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            urlShortenerController.shortenUrl(urlRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("Invalid URL or URL code", thrown.getReason());
    }

    @Test
    void testUpdateShortUrl_Success() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest();
        updateUrlRequest.setOriginalUrl("http://example.com");
        updateUrlRequest.setCustomShortCode("newShortCode");
        UrlMapping updatedMapping = new UrlMapping("newShortCode", "http://example.com");

        when(urlShortenerService.existsByOriginalUrl("http://example.com")).thenReturn(true);
        when(urlShortenerService.updateShortCodeForOriginalUrl("http://example.com", "newShortCode")).thenReturn(updatedMapping);

        ResponseEntity<String> response = urlShortenerController.updateShortUrl(updateUrlRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        assertEquals(baseUrl + "/" + "newShortCode", response.getBody());
    }

    @Test
    void testUpdateShortUrl_OriginalUrlNotFound() {
        UpdateUrlRequest updateUrlRequest = new UpdateUrlRequest();
        updateUrlRequest.setOriginalUrl("http://example.com");
        updateUrlRequest.setCustomShortCode("newShortCode");

        when(urlShortenerService.existsByOriginalUrl("http://example.com")).thenReturn(false);

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            urlShortenerController.updateShortUrl(updateUrlRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("Original URL not found", thrown.getReason());
    }

    @Test
    void testRedirectToOriginalUrl_Found() throws Exception {
        String shortCode = "short123";
        UrlMapping urlMapping = new UrlMapping(shortCode, "http://example.com");

        when(urlShortenerService.getOriginalUrl(shortCode)).thenReturn(Optional.of(urlMapping));

        var redirectView = urlShortenerController.redirectToOriginalUrl(shortCode);

        assertNotNull(redirectView);
        assertEquals("http://example.com", redirectView.getUrl());
    }

    @Test
    void testRedirectToOriginalUrl_NotFound() throws Exception {
        String shortCode = "short123";

        when(urlShortenerService.getOriginalUrl(shortCode)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            urlShortenerController.redirectToOriginalUrl(shortCode);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
