package com.alleynejr.brainmesh_backend.model;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table()
public class RefreshToken {

    @Column(nullable = false, unique = true)
    String token;
    Instant expiryDate;
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
