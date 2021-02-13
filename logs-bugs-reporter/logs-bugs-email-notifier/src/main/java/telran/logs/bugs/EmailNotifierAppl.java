package telran.logs.bugs;

import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
public class EmailNotifierAppl {
	@Autowired
    EmailProviderClient emailClient;
	@Autowired
	JavaMailSender mailSender;

	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierAppl.class, args);

	}
	@Bean
	Consumer<LogDto> getexceptionConsumer() {
		return this::takeLogAndSendMail;
		
	}
	void takeLogAndSendMail(LogDto logDto) {
		String email = emailClient.getEmailByArtifact(logDto.artifact);
	
		//TODO branch of case email in empty string
		takeLogAndSendMail(logDto, email);
	}
	private void takeLogAndSendMail(LogDto logDto, String email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("exception");
		message.setTo(email);
		message.setText("text");
		mailSender.send(message);		
	}
}