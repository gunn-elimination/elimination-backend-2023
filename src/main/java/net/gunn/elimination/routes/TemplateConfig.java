package net.gunn.elimination.routes;

import net.gunn.elimination.EliminationManager;
import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.model.Announcement;
import net.gunn.elimination.model.EliminationUser;
import net.gunn.elimination.routes.game.ScoreboardController;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice(annotations = Controller.class)
@Configuration
public class TemplateConfig {
    private final EliminationManager eliminationManager;
    private final ScoreboardController scoreboardController;
    private final AnnouncementController announcementController;

    public TemplateConfig(EliminationManager eliminationManager, ScoreboardController scoreboardController, AnnouncementController announcementController) {
        this.eliminationManager = eliminationManager;
        this.scoreboardController = scoreboardController;
        this.announcementController = announcementController;
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

    @ModelAttribute("roles")
    public Set<String> roles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @ModelAttribute("announcements")
    public List<Announcement> announcements() {
        return announcementController.announcements();
    }
}