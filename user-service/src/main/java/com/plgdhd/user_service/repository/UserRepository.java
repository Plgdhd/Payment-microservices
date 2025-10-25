package com.plgdhd.user_service.repository;

import com.plgdhd.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailNative(String email);


    @Query("SELECT u FROM User u WHERE u.surname LIKE %:surname%")
    List<User> findBySurnameLike(@Param("surname") String surname);
}
