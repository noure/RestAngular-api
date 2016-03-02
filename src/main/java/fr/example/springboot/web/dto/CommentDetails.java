package fr.example.springboot.web.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author Nour LABIHI
 *
 */
public class CommentDetails implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private String content;

    private SimpleUserDetails createdBy;

    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public SimpleUserDetails getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(SimpleUserDetails createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "CommentDetails{" + "id=" + id + ", content=" + content + ", createdBy=" + createdBy + ", createdOn=" + createdDate + '}';
    }
}
