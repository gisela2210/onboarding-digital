package com.casystem.onboarding.service;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class TokenServiceTest{
 private TokenService service(){return new TokenService("una-clave-de-prueba-suficientemente-larga");}
 @Test void creaYValidaToken(){TokenService s=service();String t=s.create(7L,"a@b.com");assertEquals(7L,s.userId("Bearer "+t));}
 @Test void rechazaTokenAlterado(){assertThrows(SecurityException.class,()->service().userId("Bearer a.b.c"));}
 @Test void rechazaTokenAusente(){assertThrows(SecurityException.class,()->service().userId(null));}
 @Test void rechazaEsquemaIncorrecto(){assertThrows(SecurityException.class,()->service().userId("Basic abc"));}
 @Test void eliminaComillasDelEmail(){TokenService s=service();String t=s.create(9L,"a\"@b.com");assertEquals(9L,s.userId("Bearer "+t));}
}
