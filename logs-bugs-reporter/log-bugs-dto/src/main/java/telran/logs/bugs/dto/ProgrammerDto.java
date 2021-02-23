package telran.logs.bugs.dto;

import javax.validation.constraints.*;

public class ProgrammerDto {
	@Min(1)
	public long id;
	@NotEmpty
	public String name;
	@Email
	public String email;
	public ProgrammerDto(@NotEmpty String name, @Email String email) {
		super();
		this.name = name;
		this.email = email;
	}
	public ProgrammerDto() {
	
	}
	

}
