package com.gaggledemo.controllers;

import com.gaggledemo.controllers.request.AppUserRequestDto;
import com.gaggledemo.controllers.request.DocumentRequestDto;
import com.gaggledemo.controllers.request.DocumentUpdateRequestDto;
import com.gaggledemo.data.AppUser;
import com.gaggledemo.data.Document;
import com.gaggledemo.service.AppUserService;
import com.gaggledemo.service.DocumentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Main class to house API endpoints
 * @see com.gaggledemo.controllers.config.SecurityConfig for auth details
 * @author jason
 */
@RestController
@RequestMapping("/api/v1")
public class MainController {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AppUserService appUserService;
    private final DocumentService docService;
    private final LocalDateTime serverStartTime;

    @Autowired
    public MainController(AppUserService appUserService, DocumentService docService) {
        this.appUserService = appUserService;
        this.docService = docService;
        serverStartTime = LocalDateTime.now();
    }

    /**
     * Returns status, version and uptime
     */
    @GetMapping("/public/info")
    public ResponseEntity<Map<String, String>> publicInfo() {
        logger.info("Public info called");
        Duration uptime = Duration.between(serverStartTime, LocalDateTime.now());

        Map<String, String> publicApiData = Map.of(
                "status", "UP",
                "version", "1.0",
                "uptime", uptime.toString());

        return ResponseEntity.ok(publicApiData);
    }

    /*
    TODO
    No deletions exist yet but this simple endpoint will allow us to verify the security config for admins
     */
    @DeleteMapping("/test")
    public ResponseEntity<String> testDelete() {
        return ResponseEntity.ok("Delete called successfully");
    }

    /********* DOCUMENT ENDPOINTS *********/

    @PostMapping("/document")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Document> createDocument(@Valid @RequestBody DocumentRequestDto dto) {
        logger.info("New document endpoint hit with title {}", dto.title);
        Document doc = docService.createDocument(dto);
        return ResponseEntity.ok(doc);
    }

    @PutMapping("/document/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Document> updateDocument(@PathVariable Integer id, @Valid @RequestBody DocumentUpdateRequestDto dto) {
        logger.info("Document update endpoint hit for id {}", id);
        Document doc = docService.updateDocument(id, dto);
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/document")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Document>> listDocuments() {
        logger.info("List documents endpoint called...");
        List<Document> docs = docService.listDocuments();
        return ResponseEntity.ok(docs);
    }

    @GetMapping("/document/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Document> getDocumentById(@PathVariable Integer id) {
        logger.info("Document lookup called for id {}", id);
        return docService.findDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*********  APP USER ENDPOINTS *********/

    @PostMapping("/appUser")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AppUser> createAppUser(@Valid @RequestBody AppUserRequestDto dto) {
        logger.info("New app user {} requested for creation", dto.name);
        AppUser user = appUserService.createAppUser(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/appUser")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AppUser>> listAppUsers() {
        logger.info("List app users called ...");
        List<AppUser> users = appUserService.listAppUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("appUser/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AppUser> getAppUserById(@PathVariable Integer id) {
        logger.info("App User lookup called using Id {}", id);
        return appUserService.findAppUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
