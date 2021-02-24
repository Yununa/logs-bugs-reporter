package telran.logs.bugs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static telran.logs.bugs.api.DtoConstants.*;

@SpringBootApplication
@RestController
public class AssignerMailProviderAppl {
	Logger LOG = LoggerFactory.getLogger(AssignerMailProviderAppl.class);
	@Value("${app-assigner-mail:logs.bugs.reporter+assigner@gmail.com}")
	String assignerMail;

	public static void main(String[] args) {
		SpringApplication.run(AssignerMailProviderAppl.class, args);

	}
	
	@GetMapping(MAIL_ASSIGNER)
	String getAssignerMail() {
		LOG.debug("\nassigner mail is {}", assignerMail);
		return assignerMail;
	}
}