package mc.bc.ms.family.app.models;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class Member {
	@NotBlank
	private String id;
	@NotBlank
	private String relationship;
}
