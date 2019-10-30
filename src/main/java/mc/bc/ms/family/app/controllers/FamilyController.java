package mc.bc.ms.family.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mc.bc.ms.family.app.services.FamilyService;

@RestController
@RequestMapping("/families")
public class FamilyController {
	@Autowired
	private FamilyService famServ;
}
