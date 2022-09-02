package net.gunn.elimination.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Announcement {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;
    @Column
    private String body;

    @Column
    private boolean active;

    @Column
    private Date startDate;
    @Column
    private Date endDate;

    @Override
    public String toString() {
        return """
            ## %s
            %s
            """.formatted(title, body);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public Announcement() {}
    public Announcement (String title, String body, Date startDate, Date endDate, boolean active) {
        this.title = title;
        this.body = body;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }
    
    public String getTitle() {
        return title;
    }
    public String getBody() {
        return body;
    }
    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean shouldDisplayToNonAdmins() {
        return active && startDate.before(new Date(System.currentTimeMillis())) && endDate.after(new Date(System.currentTimeMillis()));
    }
}
