package mc.bc.ms.family.app.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "families")
public class Family {
	private String id;
	private String institute;
	private String type;
	private List<Member> family;
}
