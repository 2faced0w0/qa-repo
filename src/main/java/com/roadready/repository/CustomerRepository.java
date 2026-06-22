package com.roadready.repository;

import com.roadready.model.Customer;
import com.roadready.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    @Query("SELECT c FROM Customer c WHERE c.user = :user")
    Optional<Customer> findByUser(@Param("user") User user);
}
