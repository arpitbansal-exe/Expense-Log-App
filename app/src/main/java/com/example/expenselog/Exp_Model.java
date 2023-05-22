package com.example.expenselog;

public class Exp_Model {
    private String id;
    private String Date;
    private String des;

    private String exp_type;
    private int value;


    public String getDate() {
        return Date;
    }

    public String getId() {
        return id;
    }

    public String getDes() {
        return des;
    }

    public int getValue() {
        return value;
    }

    public String getExp_type() {
        return exp_type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setExp_type(String exp_type) {
        this.exp_type = exp_type;
    }
}
