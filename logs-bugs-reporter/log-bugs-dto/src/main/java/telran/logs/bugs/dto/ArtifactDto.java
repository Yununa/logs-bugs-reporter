package telran.logs.bugs.dto;

import javax.validation.constraints.*;

public class ArtifactDto {
	@NotEmpty
	public String artifactId;
	@Min(1)
	public long programmerId;
	public ArtifactDto(@NotEmpty String artifactId, @Min(1) long programmerId) {
		super();
		this.artifactId = artifactId;
		this.programmerId = programmerId;
	}
}
