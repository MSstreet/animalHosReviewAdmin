package com.example.animalhosreviewadmin.repository;

import com.example.animalhosreviewadmin.domain.ReceiptStatus;
import com.example.animalhosreviewadmin.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReceiptStatusAndDeleteYnFalse(ReceiptStatus status);
    List<Review> findByDeleteYnFalse();
} 