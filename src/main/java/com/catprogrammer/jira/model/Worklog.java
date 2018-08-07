
package com.catprogrammer.jira.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Worklog data class
 * 
 * @author NatahanKun
 *
 */
public class Worklog {

    @SerializedName("key")
    @Expose
    private String key;
    
    @SerializedName("summary")
    @Expose
    private String summary;
    
    @SerializedName("entries")
    @Expose
    private List<Entry> entries;
    
    @SerializedName("fields")
    @Expose
    private Object fields;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public Object getFields() {
        return fields;
    }

    public void setFields(Object fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("key", key).append("summary", summary).append("entries", entries).append("fields", fields).toString();
    }

}
