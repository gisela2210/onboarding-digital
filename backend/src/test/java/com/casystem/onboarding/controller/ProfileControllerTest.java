package com.casystem.onboarding.controller;

import com.casystem.onboarding.dto.ProfileRequest;
import com.casystem.onboarding.model.User;
import com.casystem.onboarding.repository.UserRepository;
import com.casystem.onboarding.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {
 @Mock UserRepository users; @Mock CryptoService crypto; @Mock AuditService audit; @Mock TokenService tokens; @Mock HttpServletRequest req;
 ProfileController c; User u;
 @BeforeEach void init(){MockitoAnnotations.openMocks(this); c=new ProfileController(users,crypto,audit,tokens); u=new User();u.setId(7L);u.setEmail("u@test.local");u.setName("Usuario");when(tokens.userId(anyString())).thenReturn(7L);when(users.findById(7L)).thenReturn(Optional.of(u));when(req.getRemoteAddr()).thenReturn("127.0.0.1");when(crypto.encrypt(anyString())).thenAnswer(i->"E:"+i.getArgument(0));when(crypto.decrypt(anyString())).thenAnswer(i->{String s=i.getArgument(0);return s.startsWith("E:")?s.substring(2):s;});when(users.saveAndFlush(any())).thenAnswer(i->i.getArgument(0));}
 @Test void guardaYRecuperaPerfil(){ProfileRequest p=new ProfileRequest("Gisela","2291234567","Av. Central 123","91810","Veracruz de Ignacio de la Llave","Veracruz","Playa Linda","ABC123");var out=c.update("Bearer x",p,req);assertEquals("Información guardada correctamente",out.get("message"));var prof=(Map<?,?>)out.get("profile");assertEquals("2291234567",prof.get("phone"));assertEquals("91810",prof.get("postalCode"));assertEquals(true,prof.get("complete"));verify(users).saveAndFlush(u);verify(audit).log(eq(7L),anyString(),eq("127.0.0.1"));assertEquals("ABC123",c.get("Bearer x").get("documentNumber"));}
 @Test void validaTelefonoCpDireccionDocumentoYNombre(){assertThrows(IllegalArgumentException.class,()->c.update("h",new ProfileRequest("Gisela","123","","","","","",""),req));assertThrows(IllegalArgumentException.class,()->c.update("h",new ProfileRequest("Gisela","","x","","","","",""),req));assertThrows(IllegalArgumentException.class,()->c.update("h",new ProfileRequest("Gisela","","","1234","","","",""),req));assertThrows(IllegalArgumentException.class,()->c.update("h",new ProfileRequest("1","","","","","","",""),req));assertThrows(IllegalArgumentException.class,()->c.update("h",new ProfileRequest("Gisela","","","","","","","x".repeat(81)),req));}
 @Test void borradoLogico(){var out=c.delete("h",req);assertTrue(u.isDeleted());assertEquals("Cuenta eliminada lógicamente",out.get("message"));verify(users).save(u);}
}
