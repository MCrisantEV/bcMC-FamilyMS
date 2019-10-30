package mc.bc.ms.family.app.models;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "families")
public class Family {
	@NotBlank
	private String id;
	@NotBlank
	private String institute;
	@NotBlank
	private String type;
	private List<Member> familyMembers;
}
