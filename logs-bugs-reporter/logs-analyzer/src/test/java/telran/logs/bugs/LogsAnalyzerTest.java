package telran.logs.bugs;

import java.util.Date;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import telran.logs.bugs.dto.*;
import org.springframework.cloud.stream.binder.test.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	@Autowired
	InputDestination producer;
	@Autowired
	OutputDestination consumer;
	@Value("${app-binding-name-exceptions:exceptions-out-0}")
	String bindingNameExceptions;
	@Value("${app-binding-name-logs:log-out-0}")
	String bindingNameLogs;
	@Value("${app-logs-provider-artifact:logs-provider}")
	String logsProviderArtifact;

	@Test
	void analyzerTestNonException() {
		/* logDto is valid & no exceptions */
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
		sendLogDto(logDto);
		Message<byte[]> message = consumer.receive(0, bindingNameExceptions);
		assertNull(message);
		message = consumer.receive(0, bindingNameLogs);
		assertNotNull(message);
		LOG.debug("\n log receved in consumer: {};\n with binding name logs: {}\n", new String(message.getPayload()),
				bindingNameLogs);
	}

	@Test
	void analyzerTestException() {
		/* logDto is valid and exception */
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		sendLogDto(logDto);
		Message<byte[]> message = consumer.receive(0, bindingNameExceptions);
		assertNotNull(message);
		LOG.debug("\n log exception receved in consumer: {};\n with binding name exception: {}\n",
				new String(message.getPayload()), bindingNameExceptions);
		message = consumer.receive(0, bindingNameLogs);
		assertNotNull(message);
		LOG.debug("\n log receved in consumer: {};\n with binding name logs: {}\n", new String(message.getPayload()),
				bindingNameLogs);
	}

	@Test
	void testDateNull() {
		/* logDto without date */
		LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 0, "");
		sendLogDto(logDto);
		testLogDtoNotNormal();

	}

	@Test
	void testArtifactIsempty() {
		/* logDto with empty artifact */
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, null, 0, "");
		sendLogDto(logDto);
		testLogDtoNotNormal();

	}

	@Test
	void testLogTypeIsNull() {
		/* logDto without logType */
		LogDto logDto = new LogDto(new Date(), null, "artifact", 0, "");
		sendLogDto(logDto);
		testLogDtoNotNormal();
	}

	private void testLogDtoNotNormal() {
		/* testing wrong logDto */
		Message<byte[]> exceptionMessage = consumer.receive(0, bindingNameExceptions);
		assertNotNull(exceptionMessage);
		assertNotNull(consumer.receive(0, bindingNameLogs));
		String errorMessage = new String(exceptionMessage.getPayload());
		LOG.debug("\n exception log: {};\n sent to binding name:{}\n", errorMessage, bindingNameExceptions);
		assertTrue(errorMessage.contains(logsProviderArtifact));
		assertTrue(errorMessage.contains(LogType.BAD_REQUEST_EXCEPTION.toString()));
	}

	private void sendLogDto(LogDto logDto) {
		producer.send(new GenericMessage<LogDto>(logDto));
	}
}