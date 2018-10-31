package com.catprogrammer.jira.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * DTO of Html table cell
 * @author NathanKun
 *
 */
public class Cell {
    
    @SerializedName("displayValue")
    @Expose
    private String displayValue;
    
    @SerializedName("data")
    @Expose
    private String data;
    
    @SerializedName("type")
    @Expose
    private CellType type;
    
    public Cell(String displayValue, String data, CellType type) {
        super();
        this.displayValue = displayValue;
        this.data = data;
        this.type = type;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public CellType getType() {
        return type;
    }
    
    public void setType(CellType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("displayValue", displayValue).append("data", data).append("type", type).toString();
    }
}
