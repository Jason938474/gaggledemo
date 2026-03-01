package com.gaggledemo.service;

import com.gaggledemo.controllers.request.DocumentRequestDto;
import com.gaggledemo.controllers.request.DocumentUpdateRequestDto;
import com.gaggledemo.data.AppUser;
import com.gaggledemo.data.AppUserRepository;
import com.gaggledemo.data.Document;
import com.gaggledemo.data.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    protected DocumentRepository docRepo;

    @Mock
    protected AppUserRepository appUserRepo;

    @InjectMocks
    protected DocumentService svc;

    @Captor
    protected ArgumentCaptor<Document> docCaptor;

    // Set up some reusable test data
    protected static final AppUser creator = AppUser.builder()
            .name("creator")
            .email("create@create.com")
            .schoolAccount("ACC1").build();

    protected static final AppUser editor = AppUser.builder()
            .name("editor")
            .email("edit@edit.com")
            .schoolAccount("ACC2").build();

    @Test
    public void testNewDocHappyCase() {
        doReturn(Optional.of(creator)).when(appUserRepo).findById(1);
        doReturn(Optional.of(editor)).when(appUserRepo).findById(2);
        DocumentRequestDto dto = new DocumentRequestDto("title1", "content1", 1, 2);
        doReturn(createMockedDocument(40)).when(docRepo).save(ArgumentMatchers.any(Document.class));

        svc.createDocument(dto);

        verify(docRepo).save(docCaptor.capture());
        // lookup should be done twice here because we look up both creator and editor
        verify(appUserRepo).findById(1);
        verify(appUserRepo).findById(2);

        // now validate that the data given to the Doc Repo is correct
        Document savedDoc = docCaptor.getValue();
        assertEquals(dto.title, savedDoc.getTitle(), "Title is incorrect");
        assertEquals(dto.content, savedDoc.getContent(), "Content is incorrect");
        assertEquals(creator, savedDoc.getCreatedBy(), "Creator is incorrect");
        assertEquals(editor, savedDoc.getLastEditedBy(), "Editor is incorrect");
    }

    @Test
    public void testNewDocCreatorNotFound() {
        final int missingCreatorId = 39;
        doReturn(Optional.empty()).when(appUserRepo).findById(missingCreatorId);
        try {
            svc.createDocument(new DocumentRequestDto("Title1", "ABCD", missingCreatorId, 1));
        } catch (ResponseStatusException e) {
            assertEquals(404, e.getStatusCode().value());
            assertEquals("CreatedBy record not found for key "+missingCreatorId, e.getReason());
        }
    }

    @Test
    public void testNewDocEditorNotFound() {
        final int missingEditorId = 55;
        doReturn(Optional.of(creator)).when(appUserRepo).findById(1);
        doReturn(Optional.empty()).when(appUserRepo).findById(missingEditorId);

        // make the call and verify the exception
        DocumentRequestDto dto = new DocumentRequestDto("Title1", "ABCD", 1, missingEditorId);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->svc.createDocument(dto));
        assertEquals(404, e.getStatusCode().value());
        assertEquals("LastEditedBy record not found for key "+missingEditorId, e.getReason());
    }

    @Test
    public void testEditDocumentNotFound() {
        final int missingDocId = 41;
        doReturn(Optional.empty()).when(docRepo).findById(missingDocId);

        // make the call and verify the exception is correct
        DocumentUpdateRequestDto dto = new DocumentUpdateRequestDto("updatedTitle", "updatedContent", 123);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> svc.updateDocument(missingDocId, dto));
        assertEquals(404, e.getStatusCode().value());
        assertEquals("Document not found: "+missingDocId, e.getReason());
    }


    @Test
    public void testEditUserNotFound() {
        final int missingEditorId = 77;
        Document doc = createMockedDocument(missingEditorId);
        doReturn(Optional.of(doc)).when(docRepo).findById(doc.getId());

        // mock the user-not-found
        doReturn(Optional.empty()).when(appUserRepo).findById(doc.getId());

        // Now make the call and verify the resulting exception
        DocumentUpdateRequestDto dto = new DocumentUpdateRequestDto("updatedTitle", "updatedContent", missingEditorId);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> svc.updateDocument(doc.getId(), dto));
        assertEquals(404, e.getStatusCode().value());
        assertEquals("Editor not found: "+missingEditorId, e.getReason());
    }

    @Test
    public void testEditHappyCase() {
        Document doc = createMockedDocument(99);
        doReturn(Optional.of(doc)).when(docRepo).findById(doc.getId());

        // Next ensure lookup of AppUser succeeds
        doReturn(Optional.of(editor)).when(appUserRepo).findById(2);

        // Attempt the save
        svc.updateDocument(doc.getId(), new DocumentUpdateRequestDto("updatedTitle", "updatedContent", 2));

        verify(docRepo).save(docCaptor.capture());
        Document savedDoc = docCaptor.getValue();

        // testing edited fields
        assertEquals("updatedTitle", savedDoc.getTitle(), "Title was not updated correctly during update");
        assertEquals("updatedContent", savedDoc.getContent(), "Content was not updated correctly during update");
        assertEquals(editor, savedDoc.getLastEditedBy());

        //testing unmodifiable fields
        assertEquals(doc.getCreatedBy(), savedDoc.getCreatedBy(), "CreatedBy incorrectly modified during update");
        assertEquals(doc.getCreatedAt(), savedDoc.getCreatedAt(), "CreatedAt incorrectly modified during update");
    }

    protected Document createMockedDocument(int id) {
        // because our builder doesn't allow manual setting of  id, createdAt or updatedAt, we mock up the return values
        Document doc = Document.builder()
                .title("title1")
                .content("content")
                .createdBy(creator)
                .lastEditedBy(creator)
                .build();
        ReflectionTestUtils.setField(doc, "id", id);
        ReflectionTestUtils.setField(doc, "createdAt", LocalDateTime.of(1999,2,15, 1,22, 33));
        ReflectionTestUtils.setField(doc, "updatedAt", LocalDateTime.of(2000,3,22, 11,5, 20));
        return doc;
    }
}
