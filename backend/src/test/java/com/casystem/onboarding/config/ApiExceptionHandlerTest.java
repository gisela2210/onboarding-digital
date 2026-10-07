package com.casystem.onboarding.config;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class ApiExceptionHandlerTest{@Test void manejaSeguridadYErrores(){ApiExceptionHandler h=new ApiExceptionHandler();assertEquals(401,h.security(new SecurityException("no")).getStatusCode().value());assertEquals(400,h.general(new IllegalArgumentException("mal")).getStatusCode().value());assertEquals(400,h.general(new RuntimeException()).getStatusCode().value());}}
