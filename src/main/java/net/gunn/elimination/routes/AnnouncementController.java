package net.gunn.elimination.routes;

import net.gunn.elimination.model.Announcement;
import net.gunn.elimination.repository.AnnouncementRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnouncementController {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @GetMapping(name = "/announcements", produces = "application/json")
    public List<Announcement> announcements() {
        return announcementRepository.findAnnouncementsForCurrentTime();
    }
}
