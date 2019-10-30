package mc.bc.ms.family.app.models;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class Person {
	@NotBlank
	private String id;
	@NotBlank
	private String names;
	@NotBlank
	private String lastNames;
	@NotBlank
	private String gender;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateBirth;
	@NotBlank
	private String typeDoc;
	
	private String institute;

	@NotBlank
	private String relationship;
	@Valid
	private List<Person> familyMembers;

}
