package net.gunn.elimination.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Role implements Serializable {
    @Column
    @Id
    private String name;

    public Role() {

    }

    public Role(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Role && ((Role) obj).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

