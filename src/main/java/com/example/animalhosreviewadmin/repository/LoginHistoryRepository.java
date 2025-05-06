package com.example.animalhosreviewadmin.repository;

import com.example.animalhosreviewadmin.domain.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
} 