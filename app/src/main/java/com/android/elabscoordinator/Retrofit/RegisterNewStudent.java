package com.android.elabscoordinator.Retrofit;

public class RegisterNewStudent {
    String roll;
    String name;
    String gender;
    String branch;
    String email;
    String year;
    String course;
    String contact;

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public RegisterNewStudent(String roll, String name, String gender, String branch, String email, String year, String course, String contact) {
        this.roll = roll;
        this.name = name;
        this.gender = gender;
        this.branch = branch;
        this.email = email;
        this.year = year;
        this.course = course;
        this.contact = contact;
    }
}
