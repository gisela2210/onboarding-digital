package com.casystem.onboarding.controller;

import com.casystem.onboarding.model.PasswordResetToken;
import com.casystem.onboarding.model.User;
import com.casystem.onboarding.repository.PasswordResetTokenRepository;
import com.casystem.onboarding.repository.UserRepository;
import com.casystem.onboarding.service.AuditService;
import com.casystem.onboarding.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController @RequestMapping("/api/auth") @CrossOrigin(origins="*")
public class PasswordRecoveryController {
    private final UserRepository users; private final PasswordResetTokenRepository resets;
    private final PasswordEncoder encoder; private final AuditService audit; private final MailService mail;
    public PasswordRecoveryController(UserRepository u, PasswordResetTokenRepository r, PasswordEncoder e, AuditService a, MailService m){users=u;resets=r;encoder=e;audit=a;mail=m;}

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody Map<String,String> x, HttpServletRequest req){
        String email=x.getOrDefault("email","").trim().toLowerCase();
        if(email.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","Captura tu correo electrónico."));
        Optional<User> found=users.findByEmailAndDeletedFalse(email);
        // Respuesta neutra para no revelar qué correos existen.
        if(found.isEmpty()) return ResponseEntity.ok(Map.of("message","Si el correo está registrado, recibirás un enlace de recuperación."));
        User u=found.get(); PasswordResetToken r=new PasswordResetToken();
        r.setUserId(u.getId()); r.setToken(UUID.randomUUID().toString()); r.setExpiresAt(LocalDateTime.now().plusMinutes(20)); r.setUsed(false);
        resets.save(r);
        try {
            mail.recovery(u.getEmail(),r.getToken());
        } catch (RuntimeException ex) {
            resets.delete(r); // no dejamos un token activo si el correo no salió
            audit.log(u.getId(),"RECUPERACION_EMAIL_FALLIDA",req.getRemoteAddr());
            return ResponseEntity.status(503).body(Map.of("error",ex.getMessage()));
        }
        audit.log(u.getId(),"RECUPERACION_EMAIL_ENVIADA",req.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message","Si el correo está registrado, recibirás un enlace de recuperación."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody Map<String,String>x,HttpServletRequest req){
        String token=x.getOrDefault("token","").trim(), pass=x.getOrDefault("password","");
        if(pass.length()<8||!pass.matches(".*[A-Za-z].*")||!pass.matches(".*\\d.*")) return ResponseEntity.badRequest().body(Map.of("error","La contraseña debe tener al menos 8 caracteres, letras y números"));
        Optional<PasswordResetToken> found=resets.findByTokenAndUsedFalse(token).filter(v->v.getExpiresAt().isAfter(LocalDateTime.now()));
        if(found.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","El enlace de recuperación es inválido o expiró."));
        PasswordResetToken r=found.get(); User u=users.findById(r.getUserId()).orElseThrow();
        u.setPassword(encoder.encode(pass)); users.save(u); r.setUsed(true); resets.save(r);
        audit.log(u.getId(),"CONTRASENA_RESTABLECIDA",req.getRemoteAddr());
        return ResponseEntity.ok(Map.of("message","Contraseña restablecida. Ya puedes iniciar sesión."));
    }
}
