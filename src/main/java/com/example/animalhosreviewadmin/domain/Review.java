package com.example.animalhosreviewadmin.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_entity")
@Getter
@Setter
public class Review {

    @Column(name = "review_idx")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PetHospital petHospitalEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer userEntity;
    private String content;
    private int priceScore;
    private int kindnessScore;
    private int effectScore;
    private float score;
    private float tmpScore;
    private String hospitalName;

    // 영수증 관련 필드 (조회용)
    private String fileName;
    private String originalFileName;
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptStatus receiptStatus = ReceiptStatus.PENDING;

    @Column
    private String verificationComment;

    @Column
    private LocalDateTime verifiedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ColumnDefault("false")
    private boolean deleteYn;

    @ColumnDefault("false")
    private boolean approveYn;
}
