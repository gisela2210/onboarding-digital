package com.casystem.onboarding.service;

import com.casystem.onboarding.model.MailSettings;
import com.casystem.onboarding.repository.MailSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class MailService {
    private final MailSettingsRepository repo;
    private final CryptoService crypto;
    private final String base;

    public MailService(MailSettingsRepository repo, CryptoService crypto,
                       @Value("${app.frontend-url}") String base) {
        this.repo = repo; this.crypto = crypto; this.base = base;
    }

    protected JavaMailSenderImpl sender(MailSettings s) {
        if (s.getSenderEmail()==null || s.getSenderEmail().isBlank() ||
            s.getEncryptedPassword()==null || s.getEncryptedPassword().isBlank())
            throw new IllegalStateException("Configura primero el correo SMTP en el panel administrador.");
        if (s.getHost()==null || s.getHost().isBlank())
            throw new IllegalStateException("El servidor SMTP es obligatorio.");

        JavaMailSenderImpl m = new JavaMailSenderImpl();
        m.setHost(s.getHost().trim());
        m.setPort(s.getPort()==null ? 587 : s.getPort());
        m.setUsername(s.getSenderEmail().trim());
        m.setPassword(crypto.decrypt(s.getEncryptedPassword()));
        m.setDefaultEncoding("UTF-8");
        Properties p = m.getJavaMailProperties();
        p.put("mail.smtp.auth", String.valueOf(s.isAuth()));
        p.put("mail.smtp.starttls.enable", String.valueOf(s.isStarttls()));
        p.put("mail.smtp.starttls.required", String.valueOf(s.isStarttls()));
        if ((s.getPort()!=null && s.getPort()==465)) {
            p.put("mail.smtp.ssl.enable", "true");
            p.put("mail.smtp.starttls.enable", "false");
            p.put("mail.smtp.starttls.required", "false");
        }
        p.put("mail.smtp.connectiontimeout", "15000");
        p.put("mail.smtp.timeout", "15000");
        p.put("mail.smtp.writetimeout", "15000");
        return m;
    }

    private void send(MailSettings s, String to, String subject, String body) {
        if (to==null || to.isBlank()) throw new IllegalStateException("El correo destino es obligatorio.");
        SimpleMailMessage x = new SimpleMailMessage();
        x.setFrom(s.getSenderEmail().trim()); x.setTo(to.trim()); x.setSubject(subject); x.setText(body);
        try { sender(s).send(x); }
        catch (Exception e) {
            String detail = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new IllegalStateException("No se pudo enviar el correo SMTP. Verifica correo, contraseña de aplicación, host y puerto." + (detail==null?"":" Detalle: "+detail));
        }
    }

    public void recovery(String to, String token) {
        MailSettings s = repo.findById(1).orElseThrow(() -> new IllegalStateException("SMTP no configurado por el administrador."));
        String link = base + "/?resetToken=" + token;
        send(s, to, "Onboarding Digital - Recuperación de contraseña",
            "Hola:\n\nRecibimos una solicitud para restablecer tu contraseña.\n"+
            "Abre el siguiente enlace; expirará en 20 minutos y solo puede utilizarse una vez:\n\n"+
            link+"\n\nSi no solicitaste este cambio, ignora este mensaje.\n\nOnboarding Digital");
    }

    public void test(MailSettings s, String to) {
        send(s, to, "Onboarding Digital - Prueba SMTP",
            "La configuración SMTP funciona correctamente. Ya puedes utilizar la recuperación de contraseña.");
    }
}
