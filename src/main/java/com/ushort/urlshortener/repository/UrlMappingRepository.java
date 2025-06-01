package com.ushort.urlshortener.repository;

import com.ushort.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
    boolean existsByOriginalUrl(String originalUrl);
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
    Optional<UrlMapping> findByShortCode(String shortCode);
}
