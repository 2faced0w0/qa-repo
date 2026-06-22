package com.roadready.repository;

import com.roadready.model.Admin;
import com.roadready.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUser(User user);

    @Query("SELECT a FROM Admin a WHERE a.user.username = :username")
    Optional<Admin> findByUserUsername(@Param("username") String username);
}