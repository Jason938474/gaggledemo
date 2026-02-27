package com.gaggledemo.data;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AppUserTest {

    @Autowired
    private AppUserRepository repo;
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
        user.setEmail("test@test.com");
        user.setName("testName123");
        user.setSchoolAccount("ACCT-999");
        user = repo.save(user);

        Optional<AppUser> copyHolder = repo.findById(user.getId());
        assertTrue(copyHolder.isPresent(), "Saved user record not found");
        AppUser copy = copyHolder.get();
        assertEquals(user.toString(), copy.toString(), "Loaded user Record incorrect");
        repo.deleteById(user.getId());
        assertTrue(repo.findById(user.getId()).isEmpty(), "User record failed to delete");
    }

}