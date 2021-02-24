package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import static telran.logs.bugs.api.DtoConstants.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
public class EmailProviderTest {
	@Autowired
	WebTestClient webClient;
	
	@Test
	@Sql("programmerArtifact.sql")
	void emailExisting() {
		webClient.get().uri(EMAIL_ARTIFACT_NUMBER + "1")
		.exchange().expectStatus().isOk()
		.expectBody(String.class).isEqualTo("moshe@gmail.com");
		
	}
	
	@Test
	@Sql("programmerArtifact.sql")
	void emailNoExisting() {
		webClient.get().uri(EMAIL_ARTIFACT_NUMBER + "1")
		.exchange().expectStatus().isOk()
		.expectBody(String.class).isEqualTo("");
	}

}
