package telran.logs.bugs.dto;

import javax.validation.constraints.*;

public class AssignBugData {
	@Min(1)
	public long bugId;
	@Min(1)
	public long programmerId;
	public String description;
	public AssignBugData(@Min(1) long bugId, @Min(1) long programmerId, String description) {
		super();
		this.bugId = bugId;
		this.programmerId = programmerId;
		this.description = description;
	}
	

}
