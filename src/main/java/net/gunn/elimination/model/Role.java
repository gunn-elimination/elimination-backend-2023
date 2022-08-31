package net.gunn.elimination.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Role {
   @Column
   @Id
   private String name;

   public Role() {

   }

   public String name() {
      return name;
   }

   public Role(String name) {
      this.name = name;
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

