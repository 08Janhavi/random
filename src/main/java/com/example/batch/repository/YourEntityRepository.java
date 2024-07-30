package com.example.batch.repository;

import com.example.batch.entity.YourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YourEntityRepository extends JpaRepository<YourEntity, Long> {
}
