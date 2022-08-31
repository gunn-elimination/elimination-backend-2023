package net.gunn.elimination.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Entity
/**
 * this is a fat and ugly class that is used to represent a user in the database.
 * it is a combination of the OidcUser and the UserDetails interface, and does way too much.
 * but it's nice because our session info is stored in the database, and we don't want to
 * have to do a bunch of extra work to get the session info.
 */
public class EliminationUser implements Serializable {
   @Id
   @Column(columnDefinition = "TEXT")
   private String subject;

   @Column
   @JsonProperty
   private String email;

   @Column
   @JsonProperty
   private String forename, surname;

   @OneToOne(mappedBy = "target", fetch = FetchType.LAZY)
   private EliminationUser targettedBy;

   @OneToOne
   private EliminationUser target;

   @ManyToOne
   private EliminationUser eliminatedBy;

   @OneToMany(mappedBy = "eliminatedBy", fetch = FetchType.EAGER)
   @JsonProperty
   private Set<EliminationUser> eliminated = ConcurrentHashMap.newKeySet();

   @ManyToMany(fetch = FetchType.EAGER)
   private Collection<Role> roles = ConcurrentHashMap.newKeySet();

   @Column
   private String eliminationCode;

   public EliminationUser() {
   }


   public String getSubject() {
	  return subject;
   }

   public String getEmail() {
	  return email;
   }

   public String getForename() {
	  return forename;
   }

   public String getSurname() {
	  return surname;
   }

   public Collection<Role> getRoles() {
	  return roles;
   }

   public EliminationUser getTarget() {
	  return target;
   }

   public void setTarget(EliminationUser target) {
	  this.target = target;
   }

   public void setTargettedBy(EliminationUser targettedBy) {
	  this.targettedBy = targettedBy;
   }

   public String getEliminationCode() {
	  return eliminationCode;
   }

   public boolean isEliminated() {
	  assert (target == null && targettedBy == null && eliminationCode == null) || (target != null && targettedBy != null && eliminationCode != null);

	  return eliminatedBy != null;
   }

   public void setEliminatedBy(EliminationUser eliminatedBy) {
	  this.eliminatedBy = eliminatedBy;
   }

   public EliminationUser(String subject, String email, String forename, String surname, String eliminationCode, Set<Role> roles
   ) {
	  this.subject = subject;
	  this.email = email;
	  this.forename = forename;
	  this.surname = surname;
	  this.eliminationCode = eliminationCode;
	  this.roles = roles == null ? ConcurrentHashMap.newKeySet() : roles;
   }

   public boolean addRole(Role role) {
	  this.roles = new HashSet<>(this.roles);
	  return this.roles.add(role);
   }

   public void removeRole(Role role) {
	  this.roles = new HashSet<>(this.roles) {{
		 remove(role);
	  }};
   }
}
