package com.gaggledemo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.invoke.MethodHandles;

/**
 * Main class co house API endpoints
 * @author jason
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @GetMapping("/ping")
    public String testMe() {
        logger.info("Ping called");
        return "hi there";
    } 
    
    
}
