package com.roadready.repository;

import com.roadready.model.RentalAgent;
import com.roadready.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalAgentRepository extends JpaRepository<RentalAgent, Integer> {

    Optional<RentalAgent> findByUser(User user);
}
