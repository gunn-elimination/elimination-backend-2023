package net.gunn.elimination.routes.game;

import net.gunn.elimination.EliminationManager;
import net.gunn.elimination.EmptyGameException;
import net.gunn.elimination.IncorrectEliminationCodeException;
import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.model.EliminationUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/game")
@PreAuthorize("@eliminationManager.gameIsOngoing() && hasRole('ROLE_PLAYER')")
public class GameController {
    public final EliminationManager eliminationManager;

    public GameController(EliminationManager eliminationManager) {
        this.eliminationManager = eliminationManager;
    }

    /**
     * @apiNote Gets the current user's elimination code.
     */
    @GetMapping(value = "/code", produces = "application/json")
    public String code(@AuthenticationPrincipal EliminationAuthentication user) {
        return user.user().getEliminationCode();
    }

    @GetMapping("/eliminate")
    @PostMapping("/eliminate")
    public String eliminate(@AuthenticationPrincipal EliminationAuthentication me, @RequestParam("code") String code) throws IncorrectEliminationCodeException, EmptyGameException {
        eliminationManager.attemptElimination(me.user(), code);
        return "Eliminated";
    }

    @GetMapping("/target")
    public EliminationUser target(@AuthenticationPrincipal EliminationAuthentication me) {
        return me.user().getTarget();
    }
}
