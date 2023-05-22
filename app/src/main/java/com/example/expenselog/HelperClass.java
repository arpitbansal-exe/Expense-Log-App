package com.example.expenselog;

public class HelperClass{
    String username,email,password;
    private int total_exp;

    private int budget;



    public HelperClass(String username, String email, String password, int total_exp,int budget) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.total_exp = total_exp;
        this.budget=budget;
    }

    public int getBudget() {
        return budget;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotal_exp() {
        return total_exp;
    }

    public void setTotal_exp(int total_exp) {
        this.total_exp = total_exp;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }
}
