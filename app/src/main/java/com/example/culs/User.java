package com.example.culs;

public class User {

    private String first_name;
    private String last_name;
    private String college;
    private String year;

    public User(){

    }

    public User(String first_name, String last_name, String college, String year) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.college = college;
        this.year = year;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
