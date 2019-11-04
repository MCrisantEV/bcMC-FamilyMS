package mc.bc.ms.family.app.services;

import java.util.Map;

import mc.bc.ms.family.app.models.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FamilyService {
	
	public Mono<Map<String, Object>> savePerson(Person person);
	
	public Flux<Person> findAllPerson(String institute, String type);

	public Flux<Person> findNames(String names, String type, String institute);
	
	public Flux<Person> findDateRanger(String firstDate, String lastDate, String type, String institute);
	
	public Mono<Map<String, Object>> updatePerson(Person person);
	
	public Flux<Person> findIdPerson(String id, String institute, String type);
	
	public Mono<Map<String, Object>> deleteStudent(String id, String institute);
	
	public Mono<Map<String, Object>> deleteTeacher(String id, String institute);
	
	public Flux<Person> findAllInstitute(String institute);
}
