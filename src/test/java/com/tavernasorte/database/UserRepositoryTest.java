package com.tavernasorte.database;

import com.tavernasorte.database.entity.User;
import com.tavernasorte.database.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test
    void shouldFindPersistedUserByGeneratedUuid() {
        User saved = repository.saveAndFlush(new User("Chris", "chris@example.com", "password-hash"));
        UUID id = saved.getId();
        entityManager.clear();

        User found = repository.findById(id).orElseThrow();

        assertNotNull(id);
        assertEquals(id, found.getId());
        assertEquals("chris@example.com", found.getEmail());
    }
}
