package net.gunn.elimination.repository;

import net.gunn.elimination.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Component
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    boolean existsByTitle(String title);

    @Query("select a from Announcement a where (a.startDate <= current_date and a.endDate > current_date) and a.active = true order by a.startDate desc")
    List<Announcement> findAnnouncementsForCurrentTime();
}
