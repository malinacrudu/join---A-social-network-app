package socialnetwork.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Event  extends Entity<Long>
{
    private String title;
    private String location;
    private String description;
    private String path;
    private Long creator;
    private List<Long> participants;
    private LocalDate start;
    private LocalDate end;

    public Event(String title, String description, Long creator,LocalDate start, LocalDate end) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.participants= new ArrayList<Long>();
        this.start = start;
        this.end = end;
        this.location="";
        this.path="";
    }

    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public Long getCreator() {
        return creator;
    }


    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }


    public void setStart(LocalDate start) {
        this.start = start;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public void addParticipant(Long p)
    {
        participants.add(p);
    }

    public void deleteParticipant(Long p)
    {
        participants.remove(p);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
       Event event1 = (Event) o;
        return event1.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
