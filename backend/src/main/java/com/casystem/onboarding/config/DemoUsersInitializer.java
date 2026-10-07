package com.casystem.onboarding.config;

import com.casystem.onboarding.model.User;
import com.casystem.onboarding.repository.UserRepository;
import com.casystem.onboarding.service.CryptoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoUsersInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final CryptoService crypto;

    public DemoUsersInitializer(UserRepository users, PasswordEncoder encoder, CryptoService crypto) {
        this.users = users;
        this.encoder = encoder;
        this.crypto = crypto;
    }

    @Override
    public void run(String... args) {
        String[][] data = {
            {"ana.lopez@demo.local","Ana López","2291234567","Av. Independencia 120","91700","Veracruz de Ignacio de la Llave","Veracruz","Centro"},
            {"carlos.mendez@demo.local","Carlos Méndez","2292345678","Costa Verde 45","94294","Veracruz de Ignacio de la Llave","Boca del Río","Costa Verde"},
            {"maria.garcia@demo.local","María García","5512345678","Reforma 210","06600","Ciudad de México","Cuauhtémoc","Juárez"},
            {"jorge.ramirez@demo.local","Jorge Ramírez","8112345678","Padre Mier 300","64000","Nuevo León","Monterrey","Monterrey Centro"},
            {"sofia.hernandez@demo.local","Sofía Hernández","3312345678","Av. Vallarta 850","44100","Jalisco","Guadalajara","Guadalajara Centro"},
            {"diego.morales@demo.local","Diego Morales","2223456789","Av. Juárez 410","72000","Puebla","Puebla","Centro"},
            {"valeria.torres@demo.local","Valeria Torres","4423456789","Av. Constituyentes 88","76000","Querétaro","Querétaro","Centro"},
            {"luis.castillo@demo.local","Luis Castillo","9993456789","Paseo de Montejo 510","97000","Yucatán","Mérida","Centro"},
            {"daniela.sanchez@demo.local","Daniela Sánchez","4493456789","Av. Madero 215","20000","Aguascalientes","Aguascalientes","Zona Centro"},
            {"ricardo.flores@demo.local","Ricardo Flores","6643456789","Blvd. Agua Caliente 950","22000","Baja California","Tijuana","Zona Centro"}
        };
        for (String[] x : data) {
            if (users.findByEmailAndDeletedFalse(x[0]).isPresent()) continue;
            User u = new User();
            u.setEmail(x[0]);
            u.setPassword(encoder.encode("Demo2026!"));
            u.setName(x[1]);
            u.setPhone(crypto.encrypt(x[2]));
            u.setAddress(crypto.encrypt(x[3]));
            u.setPostalCode(crypto.encrypt(x[4]));
            u.setState(crypto.encrypt(x[5]));
            u.setMunicipality(crypto.encrypt(x[6]));
            u.setSettlement(crypto.encrypt(x[7]));
            u.setDocumentNumber(crypto.encrypt("DEMO-" + x[0].substring(0,3).toUpperCase()));
            u.setRole("USER");
            users.save(u);
        }
    }
}
