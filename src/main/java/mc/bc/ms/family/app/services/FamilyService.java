package mc.bc.ms.family.app.services;

import java.util.Map;

import mc.bc.ms.family.app.models.Person;
import reactor.core.publisher.Mono;

public interface FamilyService {
	
	public Mono<Map<String, Object>> savePerson(Person person);
	
}
