package com.arbitaja.backend.users.APIs.requests;


public class PasswordChangeRequest {
    private Integer id;
    private String old_password;
    private String new_password;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(Integer id, String old_password, String new_password) {
        this.id = id;
        this.old_password = old_password;
        this.new_password = new_password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
