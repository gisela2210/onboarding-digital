package com.casystem.onboarding.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor
public class User { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(unique=true,nullable=false) private String email; @Column(nullable=false) private String password; private String name; private String phone; private String address; private String postalCode; private String state; private String municipality; private String settlement; private String documentNumber; private String role="USER"; private boolean deleted=false; }
