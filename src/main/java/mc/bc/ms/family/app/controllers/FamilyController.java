package mc.bc.ms.family.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mc.bc.ms.family.app.models.Family;
import mc.bc.ms.family.app.models.Person;
import mc.bc.ms.family.app.services.FamilyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/personfamily")
public class FamilyController {
	@Autowired
	private FamilyService famServ;

	@PostMapping()
	public Mono<Map<String, Object>> createPerson(@RequestBody Person person) {
		return famServ.savePerson(person);
	}

	@GetMapping("/students/{institute}")
	public Flux<Family> listInstituteStudent(@PathVariable String institute) {
		return famServ.findAllPerson(institute, "Student");
	}

	@GetMapping("/teachers/{institute}")
	public Flux<Family> listInstituteTeacher(@PathVariable String institute) {
		return famServ.findAllPerson(institute, "Teacher");
	}
	
	@GetMapping("/students/names/{names}/{institute}")
	public Flux<Person> listNamesStudent(@PathVariable String names, @PathVariable String institute) {
		return famServ.findNames(names, "Student", institute);
	}

	@GetMapping("/teachers/names/{names}/{institute}")
	public Flux<Person> listNamesTeacher(@PathVariable String names, @PathVariable String institute) {
		return famServ.findNames(names, "Teacher", institute);
	}

}
