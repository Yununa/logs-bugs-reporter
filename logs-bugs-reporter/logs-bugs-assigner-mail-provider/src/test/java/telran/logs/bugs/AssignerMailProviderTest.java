package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
public class AssignerMailProviderTest {
	@Autowired
	WebTestClient webClient;
	@Value("${app-assigner-mail:assigner@gmail.com}")
	String assignerMail;
	
	@Test
	void emailExistingTest() {
		webClient.get().uri("/mail/assigner")
		.exchange().expectStatus().isOk()
		.expectBody(String.class).isEqualTo(assignerMail);
	}
}