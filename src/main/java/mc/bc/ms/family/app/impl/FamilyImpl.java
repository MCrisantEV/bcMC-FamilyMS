package mc.bc.ms.family.app.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mc.bc.ms.family.app.repositories.FamilyRepository;

@Service
public class FamilyImpl {
	
	@Autowired
	private FamilyRepository famRep;

}
