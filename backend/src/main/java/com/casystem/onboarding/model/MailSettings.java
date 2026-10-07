package com.casystem.onboarding.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="mail_settings") @Getter @Setter @NoArgsConstructor
public class MailSettings { @Id private Integer id=1; private String provider="Gmail"; private String senderEmail; private String host="smtp.gmail.com"; private Integer port=587; private String encryptedPassword; private boolean auth=true; private boolean starttls=true; }
