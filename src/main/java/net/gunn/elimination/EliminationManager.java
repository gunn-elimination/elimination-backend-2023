package net.gunn.elimination;

import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static net.gunn.elimination.auth.Roles.PLAYER;

@Service
@Transactional
public class EliminationManager {
   @PersistenceContext
   private final EntityManager entityManager;

   private final UserRepository userRepository;

   private final Instant gameStartTime, gameEndTime;
   public EliminationManager(
		   UserRepository userRepository,
		   EntityManager entityManager,
		   @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @Value("${elimination.game-start-time}") LocalDateTime  gameStartTime,
		   @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @Value("${elimination.game-end-time}") LocalDateTime  gameEndTime
   ) {
	  this.userRepository = userRepository;
	  this.entityManager = entityManager;

	  this.gameStartTime = gameStartTime.toInstant(ZonedDateTime.now().getOffset());
	  this.gameEndTime = gameEndTime.toInstant(ZonedDateTime.now().getOffset());
   }

   public void attemptElimination(EliminationUser eliminator, String code) throws IncorrectEliminationCodeException, EmptyGameException {
	  var expectedCode = entityManager.createQuery("SELECT u.target.eliminationCode from EliminationUser u WHERE u.subject = :subject", String.class)
			  .setParameter("subject", eliminator.getSubject())
			  .getResultList();
	  if (expectedCode.isEmpty())
		 throw new EmptyGameException("User is the last man standing");

	  if (!expectedCode.get(0).equals(code))
		 throw new IncorrectEliminationCodeException("Incorrect code");

	  eliminate0(eliminator.getSubject(), eliminator.getTarget().getSubject());
   }

   void eliminate0(String eliminatorSubject, String toEliminateSubject) {
	  var toEliminate = entityManager.find(EliminationUser.class, toEliminateSubject);
	  assert !toEliminate.isEliminated();

	  var eliminator = entityManager.find(EliminationUser.class, eliminatorSubject);
	  eliminator.setTarget(toEliminate.getTarget());
	  eliminator.getTarget().setTargettedBy(eliminator);
	  userRepository.save(eliminator);

	  toEliminate.setTarget(null);
	  toEliminate.setTargettedBy(null);
	  toEliminate.setEliminatedBy(eliminator);
	  toEliminate.removeRole(PLAYER);
	  userRepository.save(toEliminate);
   }

   public boolean gameHasEnded() {
	  return Instant.now().isAfter(gameEndTime);
   }

   public boolean gameHasStarted() {
	  return Instant.now().isAfter(gameStartTime);
   }

   public boolean gameHasEnoughPlayers() {
	  return userRepository.count() > 1;
   }

   public boolean gameIsOngoing() {
	  return gameHasStarted() && !gameHasEnded() && gameHasEnoughPlayers();
   }
}
