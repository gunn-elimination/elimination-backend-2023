package net.gunn.elimination.routes.admin;

import net.gunn.elimination.model.Announcement;
import net.gunn.elimination.repository.AnnouncementRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final AnnouncementRepository announcementRepository;

    public AdminController(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @PostMapping("/announcement")
    public void announcement(HttpServletRequest req, HttpServletResponse response, @RequestParam("title") String title, @RequestParam("body") String body, @RequestParam long startTimeEpoch, @RequestParam long endTimeEpoch) throws IOException {
        if (announcementRepository.existsByTitle(title)) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Announcement with name " + title + " already exists.");
            return;
        }

        var announcement = new Announcement(title, body, new Date(startTimeEpoch), new Date(endTimeEpoch));
        announcementRepository.save(announcement);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.sendRedirect(req.getHeader("Referer"));
    }
}
