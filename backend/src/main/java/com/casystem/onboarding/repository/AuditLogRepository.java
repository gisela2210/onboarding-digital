package com.casystem.onboarding.repository;
import com.casystem.onboarding.model.AuditLog;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{
 List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
 List<AuditLog> findTop50ByOrderByCreatedAtDesc();
}
