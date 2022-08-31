package net.gunn.elimination.routes.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game")
@PreAuthorize("@eliminationManager.gameIsOngoing()")
public class ScoreboardController {
   private final UserRepository userRepository;

   public ScoreboardController(UserRepository userRepository) {
	  this.userRepository = userRepository;
   }

   @GetMapping("/scoreboard")
   @Transactional
   public Scoreboard scoreboard(@RequestParam(defaultValue = "20") int limit) {
	  limit = Math.min(Math.max(limit, 0), 100);
	  return new Scoreboard(userRepository.findTopByNumberOfEliminations().limit(limit).toList());
   }

   public record Scoreboard(@JsonProperty List<EliminationUser> users) {}
}
