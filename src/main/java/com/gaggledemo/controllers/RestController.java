package com.gaggledemo.controllers;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main class co house API endpoints
 * @author jason
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/ping")
    public String testMe() {
        return "hi there";
    } 
    
    
}
