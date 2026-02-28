package com.gaggledemo.data;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AppUserTest {
    protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private AppUserRepository repo;

    @Autowired
    private DataSource dataSource;

    /**
     * This exposes the raw connection in case we need it later
     */
    @Autowired
    public void RawConnectionService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Test
    @Disabled
    void testDataExists() {
        assertEquals(3, repo.count(), "Database has no test data");
    }

    @Test
    void testAppUserCrudCycle() {
        AppUser user = new AppUser();
        user.setName("testName123");
        user.setEmail("test@test.com");
        user.setSchoolAccount("ACCT-999");
        user = repo.save(user);

        logger.info("App User {} saved, testing save...", user.getId());
        compareUserToDB(user);

        logger.info("App User loading succeeded, testing edit...");
        user.setName("123");
        user.setEmail("email@test2.com");
        user.setSchoolAccount("9999");
        repo.save(user);
        compareUserToDB(user);

        logger.info("Editing succeeded, testing deletion");
        repo.deleteById(user.getId());
        assertTrue(repo.findById(user.getId()).isEmpty(), "User record failed to delete");
    }

    /**
     * Just loads the data from the DB and
     */
    protected void compareUserToDB(AppUser user) {
        Optional<AppUser> copyHolder = repo.findById(user.getId());
        assertTrue(copyHolder.isPresent(), "Saved user record not found");
        AppUser copy = copyHolder.get();
        assertEquals(user.toString(), copy.toString(), "Loaded user Record incorrect");
    }
}