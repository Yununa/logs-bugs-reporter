package telran.logs.bugs;

import java.util.Date;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import({TestChannelBinderConfiguration.class, MailSenderValidatorAutoConfiguration.class})
public class EmailNotifierTest {
	private static final String PROGRAMMER = "Programmer";
	private static final String ASSIGNER = "Opened Bugs Assigner";
	private static final String EMAIL_PROGRAMMER = "moshe@gmail.com";
	private static final String EMAIL_ASSIGNER = "assigner@gmail.com";
	@MockBean
	EmailProviderClient client;
	@Autowired
	InputDestination input;
	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
	.withConfiguration(GreenMailConfiguration.aConfig().withUser("log", "logs-bugs"));
	LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
	
	@Value("${app-subject:exception}")
	String subject;

	@Test
	void testSendingMailToProgrammer() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn(EMAIL_PROGRAMMER);
		normalRun(EMAIL_PROGRAMMER, PROGRAMMER);
		
	}
	@Test
	void testSendingMailToAssigner() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn("");
		when(client.getAssignerMail()).thenReturn(EMAIL_ASSIGNER);
		normalRun(EMAIL_ASSIGNER, ASSIGNER);
	}
	@Test
	void noSendingMail() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn("");
		when(client.getAssignerMail()).thenReturn("");
		input.send(new GenericMessage<LogDto>(logException));
		assertEquals(0, greenMail.getReceivedMessages().length);
	}
	
	private void normalRun( String email, String receiverName) throws MessagingException {
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(email, message.getAllRecipients()[0].toString());
		assertEquals(subject, message.getSubject());
		String text = GreenMailUtil.getBody(message);
		assertTrue(text.contains(receiverName));
		assertTrue(text.contains(logException.artifact));
		assertTrue(text.contains(logException.logType.toString()));
		assertTrue(text.contains(logException.result));
		
		
	}

}
