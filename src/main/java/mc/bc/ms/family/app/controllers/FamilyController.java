package mc.bc.ms.family.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public Flux<Person> listInstituteStudent(@PathVariable String institute) {
		return famServ.findAllPerson(institute, "Student");
	}

	@GetMapping("/teachers/{institute}")
	public Flux<Person> listInstituteTeacher(@PathVariable String institute) {
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
	
	@GetMapping("/students/dates/{firstDate}/{lastDate}/{institute}")
	public Flux<Person> listDatesStudent(@PathVariable String firstDate, @PathVariable String lastDate, @PathVariable String institute) {
		return famServ.findDateRanger(firstDate, lastDate, "Student", institute);
	}
	
	@GetMapping("/teachers/dates/{firstDate}/{lastDate}/{institute}")
	public Flux<Person> listDatesTeacher(@PathVariable String firstDate, @PathVariable String lastDate, @PathVariable String institute) {
		return famServ.findDateRanger(firstDate, lastDate, "Teacher", institute);
	}
	
	@PutMapping
	public Mono<Map<String, Object>> editPerson(@RequestBody Person person) {
		return famServ.updatePerson(person);
	}
	
	@GetMapping("/students/dni/{dni}/{institute}")
	public Flux<Person> listDniStudent(@PathVariable String dni, @PathVariable String institute) {
		return famServ.findIdPerson(dni, institute, "Student");
	}

	@GetMapping("/teachers/dni/{dni}/{institute}")
	public Flux<Person> listDniTeacher(@PathVariable String dni, @PathVariable String institute) {
		return famServ.findIdPerson(dni, institute, "Teacher");
	}
	
	@DeleteMapping("/students/{dni}/{institute}")
	public Mono<Map<String, Object>> removeStudent(@PathVariable String dni, @PathVariable String institute) {
		return famServ.deleteStudent(dni, institute);
	}
	
	@DeleteMapping("/teachers/{dni}/{institute}")
	public Mono<Map<String, Object>> removeTeacher(@PathVariable String dni, @PathVariable String institute) {
		return famServ.deleteTeacher(dni, institute);
	}

}
