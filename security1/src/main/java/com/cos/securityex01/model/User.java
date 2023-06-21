package com.cos.securityex01.model;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.Getter;

// ORM - Object Relation Mapping

@Data
@Getter
@Entity
public class User {
	@Id // primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role; //ROLE_USER, ROLE_ADMIN
	// OAuth를 위해 구성한 추가 필드 2개
	private String provider;//ex ) "google"
	private String providerId;// //gogool의 아이디넘버
	@CreationTimestamp
	private Timestamp createDate;
}
