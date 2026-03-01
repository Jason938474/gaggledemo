package com.gaggledemo.service;

import com.gaggledemo.controllers.request.DocumentRequestDto;
import com.gaggledemo.controllers.request.DocumentUpdateRequestDto;
import com.gaggledemo.data.AppUser;
import com.gaggledemo.data.AppUserRepository;
import com.gaggledemo.data.Document;
import com.gaggledemo.data.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class DocumentService {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DocumentRepository docRepo;
    private final AppUserRepository appUserRepo;

    @Autowired
    public DocumentService(DocumentRepository docRepo, AppUserRepository appUserRepo) {
        this.docRepo = docRepo;
        this.appUserRepo = appUserRepo;
    }

    @Transactional
    public Document createDocument(DocumentRequestDto dto) {
        logger.info("New Document creation for document title {}", dto.title);

        // basic validation regex-type validation already done at this point, but we
        // should also validate the incoming createdBy and lastEditedBy keys
        AppUser createdBy = appUserRepo.findById(dto.createdBy).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "CreatedBy record not found for key "+dto.createdBy)
        );

        AppUser lastEditedBy = appUserRepo.findById(dto.lastEditedBy).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "LastEditedBy record not found for key "+dto.lastEditedBy)
        );

        Document newDoc = Document.builder()
                .title(dto.title)
                .content(dto.content)
                .createdBy(createdBy)
                .lastEditedBy(lastEditedBy).build();

        newDoc = docRepo.save(newDoc);
        logger.info("New document with id {} created", newDoc.getId());
        return newDoc;
    }

    public Document updateDocument(Integer id, DocumentUpdateRequestDto dto) {
        logger.info("Document update called for id {}", id);
        Document existingDoc = docRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document " + id + " not found"));

        AppUser editor = appUserRepo.findById(dto.lastEditedBy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Editor not found: " + dto.lastEditedBy));

        logger.info("All keys validated, saving document id {}", id);
        existingDoc.setTitle(dto.title);
        existingDoc.setContent(dto.content);
        existingDoc.setLastEditedBy(editor);
        return docRepo.save(existingDoc);
    }

    public List<Document> listDocuments() {
        return StreamSupport.stream(docRepo.findAll().spliterator(), false).toList();
    }

    public Optional<Document> findDocumentById(Integer id) {
        return docRepo.findById(id);
    }

}
