
package com.catprogrammer.jira.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Worklog Entry data class
 * 
 * @author NatahanKun
 *
 */
public class Entry {

    @SerializedName("id")
    @Expose
    private long id;
    
    @SerializedName("comment")
    @Expose
    private String comment;
    
    @SerializedName("timeSpent")
    @Expose
    private long timeSpent; // in second
    
    @SerializedName("author")
    @Expose
    private String author;
    
    @SerializedName("authorFullName")
    @Expose
    private String authorFullName;
    
    @SerializedName("created")
    @Expose
    private LocalDateTime created;
    
    @SerializedName("startDate")
    @Expose
    private LocalDateTime startDate;
    
    @SerializedName("updateAuthor")
    @Expose
    private String updateAuthor;
    
    @SerializedName("updateAuthorFullName")
    @Expose
    private String updateAuthorFullName;
    
    @SerializedName("updated")
    @Expose
    private LocalDateTime updated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getUpdateAuthor() {
        return updateAuthor;
    }

    public void setUpdateAuthor(String updateAuthor) {
        this.updateAuthor = updateAuthor;
    }

    public String getUpdateAuthorFullName() {
        return updateAuthorFullName;
    }

    public void setUpdateAuthorFullName(String updateAuthorFullName) {
        this.updateAuthorFullName = updateAuthorFullName;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("comment", comment).append("timeSpent", timeSpent).append("author", author).append("authorFullName", authorFullName).append("created", created).append("startDate", startDate).append("updateAuthor", updateAuthor).append("updateAuthorFullName", updateAuthorFullName).append("updated", updated).toString();
    }

}
