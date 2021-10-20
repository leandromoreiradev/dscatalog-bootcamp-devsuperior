package com.devsuperior.dscatalog.dto;


import com.devsuperior.dscatalog.entities.User;

import java.io.Serializable;
import java.util.HashSet;


import java.util.Set;

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String lastName;
    private String email;


    Set<RoleDTO> roles = new HashSet<>();

    public UserDTO() {
    }

    public UserDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

    }

    //Metodo para converter entidade para DTO
    //Ele recebe uma entidade do tipo User e pega os atributos de dentro da entidade
    //e coloca dentro dos seus atributos correspondentes no UserDTO
    public UserDTO(User entity) {
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        //Para cada entidade role dentro da lista de roles
        //adiciona um new RoleDTO(role) na lista de roles, onde role é a entidade a ser convertida em RoleDTO
        entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
        }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }
}
