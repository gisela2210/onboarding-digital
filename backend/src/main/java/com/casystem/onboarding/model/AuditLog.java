package com.casystem.onboarding.model;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="audit_log") @Getter @Setter @NoArgsConstructor
public class AuditLog { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long userId; private String action; private String ip; private LocalDateTime createdAt=LocalDateTime.now(); }
