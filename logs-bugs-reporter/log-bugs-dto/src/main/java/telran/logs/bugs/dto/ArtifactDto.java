package telran.logs.bugs.dto;

import javax.validation.constraints.*;

public class ArtifactDto {
	@NotEmpty
	public String artifactId;
	@Min(1)
	public long programmerId;

}
