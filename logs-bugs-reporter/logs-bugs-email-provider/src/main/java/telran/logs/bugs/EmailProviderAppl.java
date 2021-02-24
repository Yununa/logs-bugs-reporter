package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.repo.ArtifactsRepository;
import static telran.logs.bugs.api.DtoConstants.*;

@SpringBootApplication
@RestController
public class EmailProviderAppl {
	@Autowired
	ArtifactsRepository artifactsRepo;
	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);

	}
	@GetMapping(EMAIL_ARTIFACT)
	String getEmail(@PathVariable(name = ARTIFACT_NAME) String artifact) {
		Artifact artifactEntity = artifactsRepo.findById(artifact).orElse(null);
		return artifactEntity == null ? "" : artifactEntity.getProgrammer().getEmail();
	}

}
