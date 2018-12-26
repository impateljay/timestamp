package com.jay.timestamp.repository;

import com.jay.timestamp.model.TinyUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TinyUrlRepository extends JpaRepository<TinyUrl, String> {
    Optional<TinyUrl> findByTinyUrl(String tinyUrl);

    Optional<TinyUrl> findByUrl(String url);
}
