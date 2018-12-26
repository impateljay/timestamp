package com.jay.timestamp.controller;

import com.jay.timestamp.model.TinyUrl;
import com.jay.timestamp.repository.TinyUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/tinyurl")
public class TinyUrlController {
    private final TinyUrlRepository tinyUrlRepository;

    @Autowired
    public TinyUrlController(TinyUrlRepository tinyUrlRepository) {
        this.tinyUrlRepository = tinyUrlRepository;
    }

    @GetMapping("/{tiny_url}")
    public ResponseEntity<Object> redirectToUrl(@PathVariable("tiny_url") String tinyUrl) {
        try {
            Optional<TinyUrl> optionalTinyUrl = tinyUrlRepository.findByTinyUrl(tinyUrl);
            if (optionalTinyUrl.isPresent()) {
                Map<String, String> map = new HashMap<>();
                map.put("url", optionalTinyUrl.get().getUrl());
                return new ResponseEntity<>(map, HttpStatus.MOVED_TEMPORARILY);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("error", "Url not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/new/{url}")
    public ResponseEntity<Object> generateTinyUrl(@PathVariable("url") String url) {
        try {
            Optional<TinyUrl> optionalTinyUrl = tinyUrlRepository.findByUrl(url);
            if (optionalTinyUrl.isPresent()) {
                Map<String, String> map = new HashMap<>();
                map.put("tiny_url", optionalTinyUrl.get().getTinyUrl());
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
                StringBuilder sb = new StringBuilder(10);
                Random random = new Random();
                for (int i = 0; i < 10; i++) {
                    char c = chars[random.nextInt(chars.length)];
                    sb.append(c);
                }
                TinyUrl tinyUrl = new TinyUrl();
                tinyUrl.setUrl(url);
                tinyUrl.setTinyUrl(sb.toString());
                tinyUrlRepository.save(tinyUrl);
                Map<String, String> map = new HashMap<>();
                map.put("tiny_url", tinyUrl.getTinyUrl());
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
