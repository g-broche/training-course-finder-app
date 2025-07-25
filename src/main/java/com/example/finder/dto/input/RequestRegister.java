package com.example.finder.dto.input;

public class RequestRegister {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String password;
    private boolean hasAcceptedGdpr;

    public RequestRegister() {
    }

    public RequestRegister(String firstName, String lastName, String displayName, String email, String password, boolean hasAcceptedGdpr) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.hasAcceptedGdpr = hasAcceptedGdpr;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public boolean getHasAcceptedGdpr() {
        return hasAcceptedGdpr;
    }

    public void setHasAcceptedGdpr(boolean hasAcceptedGdpr) {
        this.hasAcceptedGdpr = hasAcceptedGdpr;
    }
}

