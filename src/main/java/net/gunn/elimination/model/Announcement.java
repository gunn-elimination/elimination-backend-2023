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
    public Announcement (String title, String body, Date startDate, Date endDate) {
        this.title = title;
        this.body = body;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }
    
    public String getTitle() {
        return title;
    }
    public String getBody() {
        return body;
    }
    public String getStartDate() {
        return startDate.toString();
    }
}
