package mc.bc.ms.family.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mc.bc.ms.family.app.models.Person;
import mc.bc.ms.family.app.services.FamilyService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class FamilyController {
	@Autowired
	private FamilyService famServ;
	
	@PostMapping("students")
	public Mono<Map<String, Object>> createPerson(@RequestBody Person person){
		return famServ.savePerson(person);
	}
}
