package net.gunn.elimination.routes.game;

import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.EliminationManager;
import net.gunn.elimination.EmptyGameException;
import net.gunn.elimination.IncorrectEliminationCodeException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/game")
@PreAuthorize("@eliminationManager.gameIsOngoing() && hasRole('ROLE_PLAYER')")
public class PlayerController {
   public final EliminationManager eliminationManager;

   public PlayerController(EliminationManager eliminationManager) {
	  this.eliminationManager = eliminationManager;
   }

   /**
	* @apiNote
	* Gets the current user's elimination code.
	*/
   @GetMapping("/code")
   public String code(@AuthenticationPrincipal EliminationAuthentication user) {
	  return user.user().getEliminationCode();
   }

   @GetMapping("/eliminate")
   public void eliminate(@AuthenticationPrincipal EliminationAuthentication me, @RequestParam("code") String code) throws IncorrectEliminationCodeException, EmptyGameException {
	  eliminationManager.attemptElimination(me.user(), code);
   }
}
