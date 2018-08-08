package com.catprogrammer.jira.model;

public class Cell {
    private String displayValue;
    private String data;
    private String data2;
    private CellType type;
    
    public Cell(String displayValue, String data, String data2, CellType type) {
        super();
        this.displayValue = displayValue;
        this.data = data;
        this.data2 = data2;
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
    public String getData2() {
        return data2;
    }
    public void setData2(String data) {
        this.data2 = data2;
    }
    public CellType getType() {
        return type;
    }
    public void setType(CellType type) {
        this.type = type;
    }
}
