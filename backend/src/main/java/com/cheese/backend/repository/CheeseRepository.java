package com.cheese.backend.repository;

import com.cheese.backend.entity.Cheese;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheeseRepository extends JpaRepository<Cheese, Long> {

}
