package com.tamarwell.expense_tracker.repository;

import com.tamarwell.expense_tracker.entity.Role;
import com.tamarwell.expense_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    //@Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.username = :username")
    //List<String> findRoleNamesByUsername(@Param("username") String username);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<String> findRoleNamesById(Long userId);

    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = :userId")
    Set<Role> findRolesByUserId(Long userId);

    @Modifying
    @Transactional
    void deleteUserById(Long id);

    @Modifying
    @Transactional
    void deleteUserByUsername(String username);

    @Modifying
    @Transactional
    void deleteUserByEmail(String email);

    @Modifying
    @Transactional
    void deleteAll();
}
