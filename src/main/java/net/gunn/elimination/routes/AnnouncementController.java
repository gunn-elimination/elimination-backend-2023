package net.gunn.elimination.routes;

import net.gunn.elimination.auth.EliminationAuthentication;
import net.gunn.elimination.model.Announcement;
import net.gunn.elimination.repository.AnnouncementRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static net.gunn.elimination.auth.Roles.ADMIN;

@RestController
public class AnnouncementController {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @GetMapping(name = "/announcements", produces = "application/json")
    public List<Announcement> announcements() {
        List<Announcement> result;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof EliminationAuthentication auth
        && auth.user().getRoles().contains(ADMIN))
            result = announcementRepository.findAll();
        else
            result = announcementRepository.findAnnouncementsForCurrentTime();

        result.sort((a, b) -> b.getStartDate().compareTo(a.getStartDate()));
        return result;
    }
}
