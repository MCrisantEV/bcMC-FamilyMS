package mc.bc.ms.family.app.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.client.WebClient;

import mc.bc.ms.family.app.models.Family;
import mc.bc.ms.family.app.models.Member;
import mc.bc.ms.family.app.models.Person;
import mc.bc.ms.family.app.repositories.FamilyRepository;
import mc.bc.ms.family.app.services.FamilyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FamilyImpl implements FamilyService{
	
	@Autowired
	private FamilyRepository famRep;
	
	@Autowired
	private WebClient client;
	
	@Autowired
	private Validator validator;

	@Override
	public Mono<Map<String, Object>> savePerson(Person person) {
		
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return Mono.just(person).flatMap(ps -> {
			Errors errors = new BeanPropertyBindingResult(ps, Person.class.getName());
			validator.validate(ps, errors);
			
			if (errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors()).map(err -> {
					String[] matriz = { err.getField(), err.getDefaultMessage() };
					return matriz;
				}).collectList().flatMap(l -> {
					respuesta.put("status", HttpStatus.BAD_REQUEST.value());
					respuesta.put("Mensaje", "Error, revise los datos");
					l.forEach(m -> {
						for (int i = 0; i < m.length; i++) {respuesta.put(m[0], m[i]);}
					});
					return Mono.just(respuesta);
				});
			}else {
				int countFam = person.getFamilyMembers().size();
				
				if(countFam == 1) {
					respuesta.put("Error", "Mínimo dos familiares");
					return Mono.just(respuesta);
				}else {
					int countParents = 0;
					
					for (Person lm : person.getFamilyMembers()) {
						if(lm.getRelationship().equals("Padre") || lm.getRelationship().equals("Madre")) {
							countParents++;
						}
					}
					
					if(countParents > 2) {
						respuesta.put("Error", "Máximo dos Padres");
						return Mono.just(respuesta);
					}else {
						List<Person> listPers = new ArrayList<>();
						Family fam = new Family();
						
						Mono.just(person).doOnNext(m -> {
							Person p = new Person();

							p.setId(m.getId());
							p.setNames(m.getNames());
							p.setLastNames(m.getLastNames());
							p.setTypeDoc(m.getTypeDoc());
							p.setGender(m.getGender());
							p.setDateBirth(m.getDateBirth());
							listPers.add(p);
							
							fam.setId(m.getId());
							fam.setInstitute(m.getInstitute());
							fam.setType(m.getRelationship());
							
							List<Member> lismemb = new ArrayList<>();
							
							m.getFamilyMembers().forEach(fe -> {
								Person pm = new Person();
								pm.setId(fe.getId());
								pm.setNames(fe.getNames());
								pm.setLastNames(fe.getLastNames());
								pm.setTypeDoc(fe.getTypeDoc());
								pm.setGender(fe.getGender());
								pm.setDateBirth(fe.getDateBirth());
								listPers.add(pm);
								
								Member memb = new Member();
								memb.setId(fe.getId());
								memb.setRelationship(fe.getRelationship());
								lismemb.add(memb);
							});
							fam.setFamilyMembers(lismemb);
						}).subscribe();
						
						return client.post()
						.accept(APPLICATION_JSON_UTF8)
						.contentType(APPLICATION_JSON_UTF8)
						.syncBody(listPers)
				     	.retrieve()
				     	.bodyToMono(Member.class)
				     	.map(rq -> {
				     		int reg = Integer.parseInt(rq.getId());
				     		if(reg == 0) {
				     			respuesta.put("Error", "No se pudo registrar.");
				     		}else {
				     			famRep.save(fam).subscribe();
				     			respuesta.put("Mensaje", "Se registro con éxito");
							}
				     		return respuesta;
				     	});
					}
					
				}
			}			
		});
	}

	
	@Override
	public Flux<Family> findAllPerson(String institute, String type) {
		return famRep.findByInstituteAndType(institute, type);
	}

	@Override
	public Flux<Person> findNames(String names, String type, String institute) {
		
		return client.get().uri("/names/{names}", Collections.singletonMap("names", names))
		.accept(APPLICATION_JSON_UTF8)
		.retrieve()
		.bodyToFlux(Person.class).flatMap(gp -> {
			return famRep.findById(gp.getId()).map(gf -> {
				gp.setInstitute(gf.getInstitute());
				gp.setRelationship(gf.getType());
				List<Person> list = new ArrayList<>();
				gf.getFamilyMembers().forEach(fr -> {
					Person p = new Person();
					p.setId(fr.getId());
					p.setRelationship(fr.getRelationship());
					list.add(p);
				});
				gp.setFamilyMembers(list);
				return gp;
			});
		})
		.filter(fil -> fil.getRelationship().equals(type))
		.filter(fil -> fil.getInstitute().equals(institute))
		.flatMap(m -> {
			if(m.getFamilyMembers().isEmpty()) {
				return Flux.just(m);
			}else {
				return Flux.fromIterable(m.getFamilyMembers()).flatMap(f -> {
					
					return client.get().uri("/{id}", Collections.singletonMap("id", f.getId()))
					.accept(APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(Person.class)
					.flatMap(gp -> {
						f.setNames(gp.getNames());
						f.setLastNames(gp.getLastNames());
						f.setGender(gp.getGender());
						f.setDateBirth(gp.getDateBirth());
						f.setTypeDoc(gp.getTypeDoc());
						return Mono.just(f);
					});
				}).collectList().map(cl -> {
					List<Person> lp = new ArrayList<>();
					cl.forEach(fr -> {
						lp.add(fr);
					});
					m.setFamilyMembers(lp);
					return m;
				});
			}
		});
	}

}
