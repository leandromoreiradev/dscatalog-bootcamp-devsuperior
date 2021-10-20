package com.devsuperior.dscatalog.dto;

import javax.validation.constraints.NotEmpty;

public class UserInsertDTO extends UserDTO{

    private static final long serialVersionUID = 1L;

    private String password;

    UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
