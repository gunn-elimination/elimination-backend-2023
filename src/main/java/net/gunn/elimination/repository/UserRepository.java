package net.gunn.elimination.repository;

import net.gunn.elimination.model.EliminationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Component
@Repository
public interface UserRepository extends JpaRepository<EliminationUser, Long> {
   boolean existsBySubject(String subject);

   Optional<EliminationUser> findBySubject(String subject);


   @Query("""
		           update EliminationUser u
		           set u.targettedBy = (SELECT newPursuer from EliminationUser newPursuer where newPursuer.subject = ?2),
		           u.target = u.targettedBy.target,
		           u.target.targettedBy = u,
		           u.targettedBy.target = u
		           WHERE u.subject = ?1
		   """)
   @Modifying
   @Transactional
   int linkUser(String subject, String newPursuerSubject);

   @Query(value = "DELETE FROM elimination_user_roles WHERE roles_name = ? AND users_subject = ?;", nativeQuery = true)
   @Modifying
   void deleteUserRole(String role, String subject);

   @Query("select u from EliminationUser u order by u.eliminated.size desc")
   Stream<EliminationUser> findTopByNumberOfEliminations();
}
