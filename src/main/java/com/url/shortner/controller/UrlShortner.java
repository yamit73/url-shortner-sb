package com.url.shortner.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UrlShortner {

    @PostConstruct
    private void runAfter(){
        System.out.println("Run after bean initialization");
    }
}
