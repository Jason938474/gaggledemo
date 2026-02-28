package com.gaggledemo.data;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DocumentTest {

    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * We'll need both of these in the test because we need to create existing users for referential integrity.
     */
    @Autowired
    protected AppUserRepository userRepo;

    @Autowired
    protected DocumentRepository docRepo;

    @Autowired
    private EntityManager entityManager;

    /**
     * This ensures that we have relational data loading working as expected.  We need @Transactional here
     * to keep the connection open for lazy loading tests
     */
    @Test
    @Transactional
    public void testAppUserLoading() {
        // First create users and document
        AppUser creator = AppUser.builder().name("Creator").email("create@create.com").schoolAccount("X").build();
        userRepo.save(creator);
        AppUser editor = AppUser.builder().name("Editor").email("edit@edit.com").schoolAccount("Y").build();
        userRepo.save(editor);

        logger.info("User {} saved, creating new document...", creator.getId());

        Document doc = Document.builder()
                .title("Test Title")
                .content("Content 1")
                .createdBy(creator)
                .lastEditedBy(editor)
                .build();

        // We need to save and reload to make sure entity loading is working.  We also do a flush and clear
        // in the entityManager's cache to make sure we can test lazy loading is working
        doc = docRepo.save(doc);
        entityManager.flush();
        entityManager.clear();

        Optional<Document> docHolder = docRepo.findById(doc.getId());
        assertTrue(docHolder.isPresent(), "Reload failed during lazy loading test");
        doc = docHolder.get();

        logger.info("Document {} created - testing lazy loading", doc.getId());
        assertFalse(Hibernate.isInitialized(doc.getCreatedBy()), "Lazy-loading of creator failed");
        assertFalse(Hibernate.isInitialized(doc.getLastEditedBy()), "Lazy-loading of creator failed");

        logger.info("Checking all data in createdBy");
        assertEquals(creator.toString(), doc.getCreatedBy().toString(), "Error during recall of creator data");

        logger.info("CreatedBy succeeded, checking all data in lastEditedBy");
        assertEquals(editor.toString(), doc.getLastEditedBy().toString(), "Error during recall of editor data");
    }

    /**
     * This is a bit longer of a test, but we need consistency of data to do a create-edit cycle.
     * We could potentially split this up and give ordering to each method, but I'm not sure if it would
     * buy us that much, so I'm leaving it as one longer unit test for now.
     */
    @Test
    public void testDocumentHappyPath() throws Exception {
        // TODO: if the test somehow fails, we may have partial data in the DB so later we may want to do
        // TODO: a cleanup, but since we have an in-memory DB, we don't need to worry about that just yet.

        logger.info("Starting Crud test, creating 2 users");

        // First, create a couple of users, one that's for creating a doc and one that edits a doc
        AppUser creator = AppUser.builder().name("Creator").email("create@create.com").schoolAccount("X").build();
        userRepo.save(creator);
        AppUser editor = AppUser.builder().name("Editor").email("edit@edit.com").schoolAccount("Y").build();
        userRepo.save(editor);

        logger.info("2 users {} and {} saved, creating new document...", creator.getId(), editor.getId());

        // On the first save, we'll have the creator in createdBy and lastEditedBy
        Document doc = Document.builder()
                .title("The Doc")
                .content("Test Content "+ UUID.randomUUID())
                .createdBy(creator)
                .lastEditedBy(creator)
                .build();

        doc = docRepo.save(doc);

        logger.info("Document ID {} saved, reloading and comparing", doc.getId());
        compareDocToDb(doc);

        logger.info("Loaded document comparison succeeded, doing the document edit");
        doc.setContent("Updated content");
        doc.setTitle("Updated title");
        doc.setLastEditedBy(editor);

        // wait for 1 sec just to make sure the updated timestamp changes then save
        TimeUnit.SECONDS.sleep(1L);
        doc = docRepo.save(doc);

        logger.info("Document ID {} updated, comparing to DB", doc.getId());
        compareDocToDb(doc);

        // Now let's ensure that the fields were updated as expected
        assertEquals("Updated title", doc.getTitle(), "Title wasn't updated");
        assertEquals("Updated content", doc.getContent(), "Document content wasn't updated");
        assertTrue(doc.getUpdatedAt().isAfter(doc.getCreatedAt()), "Updated timestamp isn't correct after save");

        logger.info("Update test complete, testing document deletion");
        docRepo.delete(doc);
        assertTrue(docRepo.findById(doc.getId()).isEmpty(), "Deleted document still exists in the database");
    }

    /**
     * Just loads another copy of the Document and does a field by field comparison.
     * If we wanted to get fancier, we could do raw SQL here to bypass the ORM and further ensure accuracy.
     */
    protected void compareDocToDb(Document doc) {
        // Load the record from the DB to ensure correctness on each field
        Optional<Document> cloneHolder = docRepo.findById(doc.getId());
        assertTrue(cloneHolder.isPresent(), "Saved document record is missing");
        Document clone = cloneHolder.get();
        // Note: timestamps come out of the DB as lower precision than when they go in.
        // To correct this we could also use ChronoUtil.MILLIS.between() but then we would be doing a field-by-field
        // comparison, which adds bulk to the test.  Instead, toString() handles data length reduction via formatting
        assertEquals(doc.toString(), clone.toString(), "Retrieved document doesn't match original");
    }

}
