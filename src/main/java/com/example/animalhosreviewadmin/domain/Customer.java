package com.example.animalhosreviewadmin.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "user_entity")
@Getter
@Setter
public class Customer {

    @Id
    @Column(name = "user_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String userId;
    private String userPw;
    private String userName;
    private String phoneNum;
    private String zipCode;
    private String addr;
    private String detailAddr;
    @ColumnDefault("false") //default 0
    private boolean social;
    private boolean deleteYn;
}
