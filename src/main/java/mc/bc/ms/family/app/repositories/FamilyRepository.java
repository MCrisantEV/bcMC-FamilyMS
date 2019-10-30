package mc.bc.ms.family.app.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import mc.bc.ms.family.app.models.Family;
import reactor.core.publisher.Flux;

public interface FamilyRepository extends ReactiveMongoRepository<Family, String>{
	
	public Flux<Family> findByInstituteAndType(String institute, String type);
	
//	public Flux<>

}
