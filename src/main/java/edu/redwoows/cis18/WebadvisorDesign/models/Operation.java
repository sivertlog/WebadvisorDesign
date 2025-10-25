package edu.redwoows.cis18.WebadvisorDesign.models;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "operation")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "op_id")
    private Integer opId;

    @Column(name = "op_route")
    private String opRoute;

    @Column(name = "op_type")
    private Integer opType;

    @ManyToMany
    @JoinTable(
            name = "oppermlink",
            joinColumns = @JoinColumn(name = "op_id"),
            inverseJoinColumns = @JoinColumn(name = "perm_id")
    )
    private Set<Permission> permissions;

    // Constructors
    public Operation() {}

    public Operation(String opRoute, Integer opType) {
        this.opRoute = opRoute;
        this.opType = opType;
    }

    // Getters and Setters
    public Integer getOpId() { return opId; }
    public void setOpId(Integer opId) { this.opId = opId; }

    public String getOpRoute() { return opRoute; }
    public void setOpRoute(String opRoute) { this.opRoute = opRoute; }

    public Integer getOpType() { return opType; }
    public void setOpType(Integer opType) { this.opType = opType; }

    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
}