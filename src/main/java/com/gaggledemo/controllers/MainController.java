package com.gaggledemo.controllers;

import com.gaggledemo.controllers.request.AppUserRequestDto;
import com.gaggledemo.data.AppUser;
import com.gaggledemo.service.AppUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Main class to house API endpoints
 * @author jason
 */
@RestController
@RequestMapping("/api/v1")
public class MainController {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AppUserService appUserService;

    @Autowired
    public MainController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> testMe() {
        logger.info("Ping called");
        return ResponseEntity.ok("hi there");
    }

    @PostMapping("/appUser")
    public ResponseEntity<AppUser> createAppUser(@Valid @RequestBody AppUserRequestDto dto) {
        logger.info("New app user {} requested for creation", dto.name);
        AppUser user = appUserService.createAppUser(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/appUser")
    public ResponseEntity<List<AppUser>> listAppUsers() {
        logger.info("List app users called ...");
        List<AppUser> users = appUserService.listAppUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("appUser/{id}")
    public ResponseEntity<AppUser> getAppUserById(@PathVariable Integer id) {
        logger.info("App User lookup called using Id {}", id);
        return appUserService.findAppUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
