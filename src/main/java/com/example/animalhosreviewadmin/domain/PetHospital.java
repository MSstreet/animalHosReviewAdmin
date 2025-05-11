package com.example.animalhosreviewadmin.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pet_hospital_entity")
@Getter
@Setter
public class PetHospital {

    @Column(name = "hospital_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hospitalName;
    private String sigunName;

    private String operState;
    private String hospitalNum;

    private String hospitalAddr; // 도로명 주소

    private BigDecimal hosLatitude ;

    private BigDecimal hosLongitude;
}
