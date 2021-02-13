package telran.logs.bugs;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
public class EmailNotifierAppl {
	Logger LOG = LoggerFactory.getLogger(EmailNotifierAppl.class);
	private static final String PROGRAMMER_NAME = "Programmer";
	private static final String ASSIGNER_NAME = "Opened Bugs Assigner";
	@Autowired
    EmailProviderClient emailClient;
	@Autowired
	JavaMailSender mailSender;
	@Value("${app-subject:exception}")
	String subject;
	private String name;

	public static void main(String[] args) {
		SpringApplication.run(EmailNotifierAppl.class, args);

	}
	@Bean
	Consumer<LogDto> getexceptionConsumer() {
		return this::takeLogAndSendMail;
		
	}
	void takeLogAndSendMail(LogDto logDto) {
		LOG.debug("\nLog recieved: {}\n", logDto);
		String email = emailClient.getEmailByArtifact(logDto.artifact);
		String receiverName = getName(email, name); 
		if(email.isEmpty()) {
			receiverName = getName(email, name);
			email = emailClient.getAssignerMail();
			if(email.isEmpty() || email == null) {
				
				LOG.error("\nError message: email 'to' has received neither from logs-bugs-email-provider nor from logs-bugs-assigner-mail-provider\n");
				return;
			}		
		}
		sendMail(logDto, email, receiverName);
		LOG.debug("\nExceptionLog has sent 'to': {};\nto mail: {}\n", receiverName, email);
	}
	private  String getName(String email, String receiverName) {
		if(email.isEmpty()) {
			 receiverName = ASSIGNER_NAME;
		}else
		receiverName = PROGRAMMER_NAME;
		return receiverName;
		
	}
	private void sendMail(LogDto logDto, String email, String receiverName) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setTo(email);
		message.setText(getText(logDto, receiverName));
		mailSender.send(message);		
		LOG.debug("\nSent email 'to': {};\nSubject: {}; \nText:\n{}\n", email, subject, getText(logDto, receiverName));
	}
	private String getText(LogDto logDto, String receiverName) {
		
		return String.format("Hello, %s\nException has been received \nDate: %s \nException type: %s \nArtifact: %s \nExplanation: %s ",
				receiverName, logDto.dateTime, logDto.logType, logDto.artifact, logDto.result);
	}
}