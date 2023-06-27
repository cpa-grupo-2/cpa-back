package com.biopark.cpa.repository.pessoas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.user.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);
    List<User> findAllByLevel(String level);

    @Modifying
    @Query(value = "SELECT * FROM user WHERE email = :#{#user.email} OR cpf = :#{#user.cpf}", nativeQuery = true)
    List<User> findUniqueKeys(@Param("user") User user);

    @Modifying
    @Query(value = "SELECT * FROM user WHERE email = :#{#email} OR cpf = :#{#cpf}", nativeQuery = true)
    List<User> findUniqueKeys(@Param("emai") String email, @Param("cpf") String cpf);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user (cpf, name, telefone, email, password, role, level, created_at, updated_at, deleted)"
        +" VALUES (:#{#user.cpf}, :#{#user.name}, :#{#user.telefone}, :#{#user.email}, :#{#user.password},"
        +" :#{#user.role.name}, :#{#user.level.name}, current_timestamp, current_timestamp, false)"
        +" ON DUPLICATE KEY UPDATE cpf = VALUES(cpf), name = VALUES(name), telefone = VALUES(telefone), email = VALUES(email),"
        +" password = VALUES(password), role = VALUES(role), level = VALUES(level), created_at = VALUES(created_at), updated_at = VALUES(updated_at), deleted = VALUES(deleted)", nativeQuery = true)
    void upsert(@Param("user") User user);
}
