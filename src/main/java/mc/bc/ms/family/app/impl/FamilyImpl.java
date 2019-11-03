package mc.bc.ms.family.app.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class FamilyImpl implements FamilyService {

	@Autowired
	@Qualifier("person")
	WebClient wcPerson;

	@Autowired
	@Qualifier("inscription")
	WebClient wcInscription;
	
	@Autowired
	@Qualifier("course")
	WebClient wcCourse;
	
	

	@Autowired
	private FamilyRepository famRep;

	@Autowired
	private Validator validator;

	@Override
	public Mono<Map<String, Object>> savePerson(Person person) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		if (errors(person) == null) {
			return famRep.findById(person.getId()).map(m -> {
				respuesta.put("Error", "No se puede registrar, ya existe un rgistro.");
				return respuesta;
			}).switchIfEmpty(Mono.just(person).flatMap(er -> {
				return actionPerson(person, 1);
			}));
		} else {
			return errors(person);
		}
	}

	@Override
	public Flux<Person> findAllPerson(String institute, String type) {
		return findFamily(famRep.findByInstituteAndType(institute, type));
	}

	@Override
	public Flux<Person> findNames(String names, String type, String institute) {
		String url = "/names/" + names;
		return findPerson(url, type, institute);
	}

	@Override
	public Flux<Person> findDateRanger(String firstDate, String lastDate, String type, String institute) {
		String url = "/dates/" + firstDate + "/" + lastDate;
		return findPerson(url, type, institute);
	}

	@Override
	public Mono<Map<String, Object>> updatePerson(Person person) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		if (errors(person) == null) {
			return famRep.findById(person.getId()).flatMap(m -> actionPerson(person, 2))
					.switchIfEmpty(Mono.just(person).map(er -> {
						respuesta.put("Error", "No se puede actualizar, Id no existe.");
						return respuesta;
					}));
		} else {
			return errors(person);
		}
	}

	@Override
	public Flux<Person> findIdPerson(String id, String institute, String type) {
		return findFamily(famRep.findByIdLikeAndInstituteAndType(id, institute, type));
	}

	@Override
	public Mono<Map<String, Object>> deleteStudent(String id, String institute) {
		String url = "/students/" + id + "/" + institute;
		return deletePerson(wcInscription, url, 1, id, institute);
	}
	
	@Override
	public Mono<Map<String, Object>> deleteTeacher(String id, String institute) {
		String url = "/teachers/" + id + "/" + institute;
		return deletePerson(wcCourse, url, 2, id, institute);
	}

	private Mono<Map<String, Object>> errors(Person person) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		Errors errors = new BeanPropertyBindingResult(person, Person.class.getName());
		validator.validate(person, errors);

		if (errors.hasErrors()) {
			return Flux.fromIterable(errors.getFieldErrors()).map(err -> {
				String[] matriz = { err.getField(), err.getDefaultMessage() };
				return matriz;
			}).collectList().flatMap(l -> {
				respuesta.put("status", HttpStatus.BAD_REQUEST.value());
				respuesta.put("Mensaje", "Error, revise los datos");
				l.forEach(m -> {
					for (int i = 0; i < m.length; i++) {
						respuesta.put(m[0], m[i]);
					}
				});
				return Mono.just(respuesta);
			});
		}
		return null;
	}

	private Mono<Map<String, Object>> actionPerson(Person person, int action) {
		Map<String, Object> respuesta = new HashMap<String, Object>();

		int countFam = person.getFamilyMembers().size();

		if (countFam == 1) {
			respuesta.put("Error", "Mínimo dos familiares");
			return Mono.just(respuesta);
		} else {
			int countParents = 0;

			for (Person lm : person.getFamilyMembers()) {
				if (lm.getRelationship().equals("Padre") || lm.getRelationship().equals("Madre")) {
					countParents++;
				}
			}

			if (countParents > 2) {
				respuesta.put("Error", "Máximo dos Padres");
				return Mono.just(respuesta);
			} else {
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

				return wcPerson.post().accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
						.syncBody(listPers).retrieve().bodyToMono(Member.class).map(rq -> {
							int reg = Integer.parseInt(rq.getId());
							if (reg == 0) {
								String ms = (action == 1) ? "registar" : "actualizar";
								respuesta.put("Error", "No se pudo " + ms + ".");
							} else {
								famRep.save(fam).subscribe();
								String ms = (action == 1) ? "registro" : "actualizo";
								respuesta.put("Mensaje", "Se " + ms + " con éxito");
							}
							return respuesta;
						});
			}
		}
	}

	private Flux<Person> findPerson(String url, String type, String institute) {
		return wcPerson.get().uri(url).accept(APPLICATION_JSON_UTF8).retrieve().bodyToFlux(Person.class).flatMap(gp -> {
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
		}).filter(fil -> fil.getRelationship().equals(type)).filter(fil -> fil.getInstitute().equals(institute))
				.flatMap(per -> findListMember(per));
	}

	private Flux<Person> findFamily(Flux<Family> family) {
		return family.flatMap(fam -> {
			return wcPerson.get().uri("/" + fam.getId()).accept(APPLICATION_JSON_UTF8).retrieve()
					.bodyToMono(Person.class).map(gp -> {
						Person per = new Person();
						per.setId(gp.getId());
						per.setNames(gp.getNames());
						per.setLastNames(gp.getLastNames());
						per.setGender(gp.getGender());
						per.setDateBirth(gp.getDateBirth());
						per.setTypeDoc(gp.getTypeDoc());
						per.setInstitute(fam.getInstitute());
						per.setRelationship(fam.getType());

						List<Person> listper = new ArrayList<>();

						fam.getFamilyMembers().forEach(ffam -> {
							Person p = new Person();
							p.setId(ffam.getId());
							p.setRelationship(ffam.getRelationship());
							listper.add(p);
						});
						per.setFamilyMembers(listper);
						return per;
					});
		}).flatMap(per -> findListMember(per));
	}

	private Mono<Person> findListMember(Person person) {
		return Mono.just(person).flatMap(per -> {
			if (per.getFamilyMembers().isEmpty()) {
				return Mono.just(per);
			} else {
				return Flux.fromIterable(per.getFamilyMembers()).flatMap(memb -> {
					return wcPerson.get().uri("/" + memb.getId()).accept(APPLICATION_JSON_UTF8).retrieve()
							.bodyToMono(Person.class).map(gper -> {
								memb.setNames(gper.getNames());
								memb.setLastNames(gper.getLastNames());
								memb.setGender(gper.getGender());
								memb.setDateBirth(gper.getDateBirth());
								memb.setTypeDoc(gper.getTypeDoc());
								return memb;
							});
				}).collectList().map(lmem -> {
					per.setFamilyMembers(lmem);
					return per;
				});
			}
		});
	}
	
	private Mono<Map<String, Object>> deletePerson(WebClient wc, String url, int type, String id, String institute) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		String person = (type == 1) ? "estudiante" : "profesor";
		
		return wc.get().uri(url).accept(APPLICATION_JSON_UTF8).retrieve().bodyToFlux(Member.class)
				.collectList().flatMap(list -> {
					if (list.size() > 0) {
						respuesta.put("Error", "No se puede eliminar, el "+person+" tiene cursos asignados");
						return Mono.just(respuesta);
					} else {
						return famRep.findByIdAndInstitute(id, institute).map(fam -> {
							famRep.delete(fam).subscribe();
							respuesta.put("Mensaje", "Se elimino con exito al "+person);
							return respuesta;
						}).switchIfEmpty(Mono.just("").map(m -> {
							respuesta.put("Error", "No existe el "+person);
							return respuesta;
						}));
					}
				});
	}
}