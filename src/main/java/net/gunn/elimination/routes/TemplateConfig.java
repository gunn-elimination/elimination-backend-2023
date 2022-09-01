package net.gunn.elimination.routes;

import net.gunn.elimination.EliminationManager;
import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.routes.game.ScoreboardController;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
@Configuration
public class TemplateConfig {
    private final EliminationManager eliminationManager;
    private final ScoreboardController scoreboardController;

    public TemplateConfig(EliminationManager eliminationManager, ScoreboardController scoreboardController) {
        this.eliminationManager = eliminationManager;
        this.scoreboardController = scoreboardController;
    }

    @ModelAttribute("currentUser")
    public EliminationUser getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof EliminationAuthentication auth)
            return auth.user();
        return null;
    }

    @ModelAttribute("eliminationManager")
    public EliminationManager eliminationManager() {
        return eliminationManager;
    }

    @ModelAttribute("scoreboard")
    public ScoreboardController.Scoreboard scoreboard() {
        return scoreboardController.scoreboard(20);
    }
}