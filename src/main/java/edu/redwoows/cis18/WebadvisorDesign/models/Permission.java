package edu.redwoows.cis18.WebadvisorDesign.models;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perm_id")
    private Integer permId;

    @Column(name = "perm_name")
    private String permName;

    @ManyToMany(mappedBy = "permissions")
    private Set<Component> components;

    @ManyToMany(mappedBy = "permissions")
    private Set<Operation> operations;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

    // Constructors
    public Permission() {}

    public Permission(String permName) {
        this.permName = permName;
    }

    // Getters and Setters
    public Integer getPermId() { return permId; }
    public void setPermId(Integer permId) { this.permId = permId; }

    public String getPermName() { return permName; }
    public void setPermName(String permName) { this.permName = permName; }

    public Set<Component> getComponents() { return components; }
    public void setComponents(Set<Component> components) { this.components = components; }

    public Set<Operation> getOperations() { return operations; }
    public void setOperations(Set<Operation> operations) { this.operations = operations; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}