package com.casystem.onboarding.model;
import jakarta.persistence.*;import lombok.*;import java.time.LocalDateTime;
@Entity @Table(name="password_reset_tokens") @Getter @Setter @NoArgsConstructor
public class PasswordResetToken{@Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;private Long userId;@Column(unique=true,nullable=false)private String token;private LocalDateTime expiresAt;private boolean used=false;}
