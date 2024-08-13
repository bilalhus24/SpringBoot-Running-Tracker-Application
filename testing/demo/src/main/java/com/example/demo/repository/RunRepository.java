package com.example.demo.repository;

import com.example.demo.entity.Run.Run;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunRepository extends JpaRepository<Run, Long> {

    List<Run> findByUserId(Long userId);
}
