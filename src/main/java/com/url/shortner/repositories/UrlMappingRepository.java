package com.url.shortner.repositories;

import com.url.shortner.models.UrlMapping;
import com.url.shortner.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Integer> {
    UrlMapping findByShortUrl(String shortUrl);
    List<UrlMapping> findByUser(User user);
}
