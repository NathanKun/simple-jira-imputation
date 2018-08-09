package com.catprogrammer.jira.model;

public class Cell {
    private String displayValue;
    private String data;
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
}
