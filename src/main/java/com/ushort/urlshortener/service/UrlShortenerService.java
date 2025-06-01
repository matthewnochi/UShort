package com.ushort.urlshortener.service;

import com.ushort.urlshortener.entity.UrlMapping;
import com.ushort.urlshortener.repository.UrlMappingRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public UrlMapping shortenUrl(String originalUrl) {
        if (!validateUrl(originalUrl)) {
            return null;
        }

        Optional<UrlMapping> existing = repository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            return existing.get();
        }

        String shortCode = generateShortCode(); 
        if (getOriginalUrl(shortCode).isPresent()) {
            return null;
        }

        UrlMapping mapping = new UrlMapping(shortCode, originalUrl);
        return repository.save(mapping);
    }

    @Transactional
    public UrlMapping updateShortCodeForOriginalUrl(String originalUrl, String newShortCode) {
        if (!validateUrl(originalUrl)) {
            return null;
        }
        if (newShortCode == null || !newShortCode.matches("^[a-zA-Z0-9]{4,10}$")) {
            return null;
        }

        Optional<UrlMapping> conflict = repository.findByShortCode(newShortCode);
        if (conflict.isPresent() && !conflict.get().getOriginalUrl().equals(originalUrl)) {
            return null; // Short code already in use by another URL
        }
        
        Optional<UrlMapping> existingMappingOpt = repository.findByOriginalUrl(originalUrl);
        if (existingMappingOpt.isEmpty()) {
            return null;
        }

        UrlMapping existingMapping = existingMappingOpt.get();
        existingMapping.setShortCode(newShortCode);
        return repository.save(existingMapping);
    }

    public Optional<UrlMapping> getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode);
    }

    public boolean existsByOriginalUrl(String originalUrl) {
        return repository.existsByOriginalUrl(originalUrl);
    }

    private boolean validateUrl(String url) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        return url != null && urlValidator.isValid(url);
    }

    private String generateShortCode() {
        return RandomStringUtils.randomAlphanumeric(6);
    }
}
