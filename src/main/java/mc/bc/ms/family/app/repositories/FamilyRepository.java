package mc.bc.ms.family.app.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import mc.bc.ms.family.app.models.Family;

public interface FamilyRepository extends ReactiveMongoRepository<Family, String>{

}
