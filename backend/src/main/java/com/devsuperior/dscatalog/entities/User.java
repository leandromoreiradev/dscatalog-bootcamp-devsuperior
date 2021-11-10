package com.devsuperior.dscatalog.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_user")
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(unique = true) // Torna esse atributo unico, não permitindo sua repetição no DB
    private String email;
    private String password;

    //Mapeamento muitos para muitos da tabela associativa entre user e role
    @ManyToMany(fetch = FetchType.EAGER) //Força sempre que buscar o user no DB, ja vai vir com seus roles(perfis do user)
    @JoinTable(name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"), //É o nome da FK referente a tabela da classe atual
            inverseJoinColumns = @JoinColumn(name = "role_id"))//É o nome da FK referente a tabela da role
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


    /* IMPLEMENTAÇÂO DO UserDetails */

    //Retorna uma colecao do tipo GrantedAuthority
    //Percorre a colacao de Role convertendo cada elemento do tipo role para o tipo GrantedAuthority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Tranformando a lista em stream, usando o map() = transforma cada elemento,
        // ou seja para cada elemento do tipo role tranforme em tipo GrantedAuthority
        //SimpleGrantedAuthority = classe concreta que implementa a interface GrantedAuthority
        //Recebe o nome do role que está dentro do objeto Role
        // .collect(Collectors.toList()) = transforma o stream em list novamente
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    //retona o email que é o username nesse sistema é o email
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }




}
