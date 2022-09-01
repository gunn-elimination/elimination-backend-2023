package net.gunn.elimination.routes.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/game")
@PreAuthorize("@eliminationManager.gameHasStarted()")
public class ScoreboardController {
    private final UserRepository userRepository;

    public ScoreboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/scoreboard", produces = "application/json")
    @Transactional
    @ResponseBody
    public Scoreboard scoreboard(@RequestParam(defaultValue = "20") int limit) {
        limit = Math.min(Math.max(limit, 0), 100);
        return new Scoreboard(userRepository.findTopByNumberOfEliminations().limit(limit).toList());
    }

    @GetMapping(value = "/scoreboard", produces = "text/html")
    @Transactional
    public String scoreboardRendered() {
        return "game/scoreboard";
    }

    public record Scoreboard(@JsonProperty List<EliminationUser> users) {
    }
}
