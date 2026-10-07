package com.casystem.onboarding.controller;
import com.casystem.onboarding.dto.ProfileRequest;import com.casystem.onboarding.model.User;import com.casystem.onboarding.repository.UserRepository;import com.casystem.onboarding.service.*;import jakarta.servlet.http.HttpServletRequest;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.bind.annotation.*;import java.util.*;import java.util.regex.Pattern;
@RestController @RequestMapping("/api/profile") @CrossOrigin(origins="*") public class ProfileController{
private final UserRepository users;private final CryptoService crypto;private final AuditService audit;private final TokenService tokens;
private static final Pattern NAME=Pattern.compile("^[\\p{L} .'-]{2,80}$");private static final Pattern PHONE=Pattern.compile("^\\d{10}$");private static final Pattern CP=Pattern.compile("^\\d{5}$");
public ProfileController(UserRepository u,CryptoService c,AuditService a,TokenService t){users=u;crypto=c;audit=a;tokens=t;}
private User me(String h){Long id=tokens.userId(h);return users.findById(id).filter(u->!u.isDeleted()).orElseThrow();}
private String clean(String s){return s==null?"":s.trim();}
private String dec(String s){return s==null?"":crypto.decrypt(s);}
private Map<String,Object> profile(User u){Map<String,Object>m=new LinkedHashMap<>();m.put("name",u.getName()==null?"":u.getName());m.put("email",u.getEmail());m.put("phone",dec(u.getPhone()));m.put("address",dec(u.getAddress()));m.put("postalCode",dec(u.getPostalCode()));m.put("state",dec(u.getState()));m.put("municipality",dec(u.getMunicipality()));m.put("settlement",dec(u.getSettlement()));m.put("documentNumber",dec(u.getDocumentNumber()));m.put("complete",u.getPhone()!=null&&u.getAddress()!=null&&u.getPostalCode()!=null&&u.getState()!=null&&u.getMunicipality()!=null&&u.getSettlement()!=null&&u.getDocumentNumber()!=null);return m;}
@Transactional @PutMapping public Map<String,Object> update(@RequestHeader("Authorization")String h,@RequestBody ProfileRequest x,HttpServletRequest req){
 User u=me(h);String name=clean(x.name()),phone=clean(x.phone()),address=clean(x.address()),cp=clean(x.postalCode()),state=clean(x.state()),municipality=clean(x.municipality()),settlement=clean(x.settlement()),doc=clean(x.documentNumber());
 if(!name.isBlank()){if(!NAME.matcher(name).matches())throw new IllegalArgumentException("El nombre solo puede contener letras y espacios (2 a 80 caracteres)");u.setName(name);}
 if(!phone.isBlank()){if(!PHONE.matcher(phone).matches())throw new IllegalArgumentException("El teléfono debe contener exactamente 10 dígitos");u.setPhone(crypto.encrypt(phone));}
 if(!address.isBlank()){if(address.length()<5||address.length()>180)throw new IllegalArgumentException("Captura una calle y número válidos");u.setAddress(crypto.encrypt(address));}
 if(!cp.isBlank()){if(!CP.matcher(cp).matches())throw new IllegalArgumentException("El código postal debe tener 5 dígitos");u.setPostalCode(crypto.encrypt(cp));}
 if(!state.isBlank())u.setState(crypto.encrypt(state));if(!municipality.isBlank())u.setMunicipality(crypto.encrypt(municipality));if(!settlement.isBlank())u.setSettlement(crypto.encrypt(settlement));
 if(!doc.isBlank()){if(doc.length()>80)throw new IllegalArgumentException("El número de identificación no puede exceder 80 caracteres");u.setDocumentNumber(crypto.encrypt(doc));}
 u=users.saveAndFlush(u);audit.log(u.getId(),"Información registrada/actualizada por el cliente",req.getRemoteAddr());return Map.of("message","Información guardada correctamente","profile",profile(u));}
@GetMapping public Map<String,Object> get(@RequestHeader("Authorization")String h){return profile(me(h));}
@DeleteMapping public Map<String,String> delete(@RequestHeader("Authorization")String h,HttpServletRequest req){User u=me(h);u.setDeleted(true);users.save(u);audit.log(u.getId(),"Borrado lógico de cuenta",req.getRemoteAddr());return Map.of("message","Cuenta eliminada lógicamente");}}
