package net.gunn.elimination;

import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(scripts = "classpath:setup.sql")
@Sql(scripts = "classpath:teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EliminationManagerTest {
    @Autowired
    EntityManager entityManager;
    @Autowired
        UserRepository userRepository;

    EliminationManager manager;
    @BeforeEach
    void setUp() {
        manager = new EliminationManager(
            userRepository,
            entityManager,
            LocalDateTime.now(),
            LocalDateTime.now().plus(1, java.time.temporal.ChronoUnit.DAYS)
        );
    }

    @Test
    void attemptElimination() {
        var testUser0 = userRepository.findBySubject("subject0").orElseThrow();
        var testUser1 = userRepository.findBySubject("subject1").orElseThrow();

        assertThrows(IncorrectEliminationCodeException.class, () -> manager.attemptElimination(testUser0, "incorrect-code"));
        assertThrows(IncorrectEliminationCodeException.class, () -> manager.attemptElimination(testUser1, "incorrect-code"));

        assertDoesNotThrow(() -> manager.attemptElimination(testUser0, testUser1.getEliminationCode()));

        assertNull(userRepository.findBySubject("subject0").orElseThrow().getTarget());
        assertNull(userRepository.findBySubject("subject0").orElseThrow().getTargettedBy());
        assertNull(userRepository.findBySubject("subject0").orElseThrow().getEliminationCode());

        assertNull(userRepository.findBySubject("subject1").orElseThrow().getTarget());
        assertNull(userRepository.findBySubject("subject1").orElseThrow().getTargettedBy());
        assertNull(userRepository.findBySubject("subject1").orElseThrow().getEliminationCode());

        assertThrows(EmptyGameException.class, () -> manager.attemptElimination(testUser1, "incorrect-code"));
        assertThrows(EmptyGameException.class, () -> manager.attemptElimination(testUser1, testUser0.getEliminationCode()));
    }

    @Test
    void gameHasEnded() throws IncorrectEliminationCodeException, EmptyGameException {
        var testUser0 = userRepository.findBySubject("subject0").orElseThrow();
        var testUser1 = userRepository.findBySubject("subject1").orElseThrow();

        assertFalse(manager.gameHasEnded());

        manager.attemptElimination(testUser0, testUser1.getEliminationCode());

        assertTrue(manager.gameHasEnded());
    }

    @Test
    void gameHasStarted() {
        assertTrue(manager.gameHasStarted());
        var testUser1 = userRepository.findBySubject("subject1").orElseThrow();
        userRepository.delete(testUser1);

        assertFalse(manager.gameHasStarted());
        userRepository.save(testUser1);
        assertTrue(manager.gameHasStarted());
    }

    @Test
    void gameHasEnoughPlayers() {
        assertTrue(manager.gameHasEnoughPlayers());
        var testUser1 = userRepository.findBySubject("subject1").orElseThrow();
        userRepository.delete(testUser1);

        assertFalse(manager.gameHasEnoughPlayers());
        userRepository.save(testUser1);
        assertTrue(manager.gameHasEnoughPlayers());
    }

    @Test
    void gameIsOngoing() {
        assertTrue(manager.gameIsOngoing());
        var testUser1 = userRepository.findBySubject("subject1").orElseThrow();
        userRepository.delete(testUser1);

        assertFalse(manager.gameIsOngoing());
        userRepository.save(testUser1);
        assertTrue(manager.gameIsOngoing());
    }

    @Test
    void unlink() throws IncorrectEliminationCodeException, EmptyGameException {
        // make a bunch of users
        for (int i = 9; i >= 0; i--) {
            var user = new EliminationUser();
            user.setSubject("subject" + i);
            user.setEliminationCode("code" + i);
            user.setTarget(userRepository.findBySubject("subject" + (i + 1) % 10).orElseThrow());
            userRepository.save(user);
        }

        // make them all eliminate their targets, and ensure the new targets are correct
        for (int i = 0; i < 10; i += 2) {
            var user = userRepository.findBySubject("subject" + i).orElseThrow();
            var target = user.getTarget();
            manager.attemptElimination(user, target.getEliminationCode());
            user = userRepository.findBySubject("subject" + i).orElseThrow();
            assertEquals(target.getTarget(), user.getTarget());
            assertEquals(user.getTarget().getTargettedBy(), user);

            target = userRepository.findBySubject(target.getSubject()).orElseThrow();
            assertEquals(user, target.getEliminatedBy());
        }

        for (int i = 0; i < 10; i += 4) {
            var user = userRepository.findBySubject("subject" + i).orElseThrow();
            var target = user.getTarget();
            manager.attemptElimination(user, target.getEliminationCode());
            user = userRepository.findBySubject("subject" + i).orElseThrow();
            assertEquals(target.getTarget(), user.getTarget());
            assertEquals(user.getTarget().getTargettedBy(), user);

            target = userRepository.findBySubject(target.getSubject()).orElseThrow();
            assertEquals(user, target.getEliminatedBy());
        }

        {
            int i = 4;
            var user = userRepository.findBySubject("subject" + i).orElseThrow();
            var target = user.getTarget();
            manager.attemptElimination(user, target.getEliminationCode());
            user = userRepository.findBySubject("subject" + i).orElseThrow();
            assertNull(user.getTarget());

            target = userRepository.findBySubject(target.getSubject()).orElseThrow();
            assertEquals(user, target.getEliminatedBy());
        }
        var lastUser = userRepository.findBySubject("subject4").orElseThrow();
        assertTrue(lastUser.isWinner());
        assertTrue(manager.gameHasEnded());

    }
}