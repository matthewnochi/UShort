package com.ushort.urlshortener.controllers;

import com.ushort.urlshortener.entity.UrlMapping;
import com.ushort.urlshortener.dto.UpdateUrlRequest;
import com.ushort.urlshortener.dto.UrlRequest;
import com.ushort.urlshortener.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest urlRequest) {
        String originalUrl = urlRequest.getOriginalUrl();
        UrlMapping urlMapping = urlShortenerService.shortenUrl(originalUrl);
        if (urlMapping != null) {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            return new ResponseEntity<>(baseUrl + "/" + urlMapping.getShortCode(), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL or URL code");
        }
    }

    @PutMapping("/shorten")
    public ResponseEntity<String> updateShortUrl(@RequestBody UpdateUrlRequest updateUrlRequest) {
        String originalUrl = updateUrlRequest.getOriginalUrl();
        String customShortCode = updateUrlRequest.getCustomShortCode();

        if (!urlShortenerService.existsByOriginalUrl(originalUrl)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL not found");
        }

        UrlMapping updatedMapping = urlShortenerService.updateShortCodeForOriginalUrl(originalUrl, customShortCode);

        if (updatedMapping != null) {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            return new ResponseEntity<>(baseUrl + "/" + updatedMapping.getShortCode(), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL or URL code");
        }
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) throws URISyntaxException {
        return urlShortenerService.getOriginalUrl(shortCode)
        .map(urlMapping -> {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(urlMapping.getOriginalUrl());
            return redirectView;
        })
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));
    }
}
