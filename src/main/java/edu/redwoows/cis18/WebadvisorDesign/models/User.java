package edu.redwoows.cis18.WebadvisorDesign.models;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_username")
    private String userUsername;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_password_hash")
    private String userPasswordHash;

    @Column(name = "user_salt")
    private String userSalt;

    @ManyToMany
    @JoinTable(
            name = "userrolelink",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Constructors
    public User() {}

    public User(String userUsername, String userPasswordHash, String userSalt) {
        this.userUsername = userUsername;
        this.userPasswordHash = userPasswordHash;
        this.userSalt = userSalt;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserUsername() { return userUsername; }
    public void setUserUsername(String userUsername) { this.userUsername = userUsername; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPasswordHash() { return userPasswordHash; }
    public void setUserPasswordHash(String userPasswordHash) { this.userPasswordHash = userPasswordHash; }

    public String getUserSalt() { return userSalt; }
    public void setUserSalt(String userSalt) { this.userSalt = userSalt; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

}