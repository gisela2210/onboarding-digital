package com.casystem.onboarding.service; import org.junit.jupiter.api.*; import static org.junit.jupiter.api.Assertions.*;
class CryptoServiceTest{
 private CryptoService service(){return new CryptoService("0123456789abcdef0123456789abcdef");}
 @Test void cifraYDescifra(){CryptoService c=service();String e=c.encrypt("ABC123");assertNotEquals("ABC123",e);assertEquals("ABC123",c.decrypt(e));}
 @Test void aceptaNull(){CryptoService c=service();assertNull(c.encrypt(null));assertNull(c.decrypt(null));}
 @Test void cifraBytes(){CryptoService c=service();byte[] raw={1,2,3,4};assertArrayEquals(raw,c.decryptBytes(c.encryptBytes(raw)));}
 @Test void rechazaClaveInvalida(){assertThrows(IllegalArgumentException.class,()->new CryptoService("corta"));}
 @Test void rechazaCiphertextManipulado(){CryptoService c=service();byte[] e=c.encryptBytes("secreto".getBytes());e[e.length-1]^=1;assertThrows(IllegalStateException.class,()->c.decryptBytes(e));}
}
