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
class StudentTest {

    @Autowired
    private StudentRepository repo;
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
    void testStudentCrudCycle() {
        Student st = new Student();
        st.setEmail("test@test.com");
        st.setName("testName123");
        st.setSchoolAccount("ACCT-999");
        st = repo.save(st);

        Optional<Student> copyHolder = repo.findById(st.getId());
        assertTrue(copyHolder.isPresent(), "Saved student record not found");
        Student copy = copyHolder.get();
        assertEquals(st.toString(), copy.toString(), "Loaded Student Record incorrect");
        repo.deleteById(st.getId());
        assertTrue(repo.findById(st.getId()).isEmpty(), "Student record failed to delete");
    }

}