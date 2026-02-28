package com.gaggledemo.data;

import com.gaggledemo.util.DateUtil;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATEDBY")
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LASTEDITEDBY")
    private AppUser lastEditedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Document() { }

    public Integer getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AppUser getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(AppUser lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Use Hibernate annotations to automate the "sysdate" logic
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private Document(Document.Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.createdBy = builder.createdBy;
        this.lastEditedBy = builder.lastEditedBy;
    }

    /**
     * In order to not trigger the lazy loader, we're just using getId for each of the
     * createdBy and lastEditedBy.  Also, for now, we'll output the contents of the file but if that
     * doesn't stay as simple text, we may want to exclude that later.
     */
    @Override
    public String toString() {

        return "Document{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdById=" + (createdBy != null ? createdBy.getId() : "null") +
                ", lastEditedById=" + (lastEditedBy != null ? lastEditedBy.getId() : "null") +
                ", createdAt=" + DateUtil.formatLocalDateTime(createdAt) +
                ", updatedAt =" + DateUtil.formatLocalDateTime(updatedAt) +
                '}';
    }

    public static class Builder {
        private String title;
        private String content;
        private AppUser createdBy;
        private AppUser lastEditedBy;

        public Document.Builder title(String title) {
            this.title = title;
            return this;
        }

        public Document.Builder content(String content) {
            this.content = content;
            return this;
        }

        public Document.Builder createdBy(AppUser createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Document.Builder lastEditedBy(AppUser lastEditedBy) {
            this.lastEditedBy = lastEditedBy;
            return this;
        }

        public Document build() {
            return new Document(this);
        }
    }

    // Static helper to start the builder
    public static Document.Builder builder() {
        return new Document.Builder();
    }
}