package edu.redwoows.cis18.WebadvisorDesign.models;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "component")
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_id")
    private Integer compId;

    @Column(name = "comp_name")
    private String compName;

    @Column(name = "comp_type")
    private Integer compType;

    @ManyToMany
    @JoinTable(
            name = "comppermlink",
            joinColumns = @JoinColumn(name = "comp_id"),
            inverseJoinColumns = @JoinColumn(name = "perm_id")
    )
    private Set<Permission> permissions;

    // Constructors
    public Component() {}

    public Component(String compName, Integer compType) {
        this.compName = compName;
        this.compType = compType;
    }

    // Getters and Setters
    public Integer getCompId() { return compId; }
    public void setCompId(Integer compId) { this.compId = compId; }

    public String getCompName() { return compName; }
    public void setCompName(String compName) { this.compName = compName; }

    public Integer getCompType() { return compType; }
    public void setCompType(Integer compType) { this.compType = compType; }

    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}