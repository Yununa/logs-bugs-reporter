package telran.logs.bugs;

import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.doc.LogsRepo;
import org.springframework.cloud.stream.binder.test.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	@Autowired
	InputDestination producer;
	@Autowired
	OutputDestination consumer;
	@Value("${app-binding-name-exeptions:exceptions-out-0}")
	String bindingNameExeptions;
	@Value("${app-binding-name-logs:log-out-0}")
	String bindingNameLogs;
	@Autowired
	LogsRepo logsRepo;
	@BeforeEach
	void setup() {
		logsRepo.deleteAll();
	}
	
	@Test
	void analyzerTestNonException() {
		/*logDto is valid & no exceptions*/
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
	    sendLogDto(logDto);
	    Message<byte[]> message = consumer.receive(0, bindingNameExeptions);
	    assertNull(message);   
	    message = consumer.receive(0, bindingNameLogs);
	    assertNotNull(message); 
	    LOG.debug("\n log receved in consumer {}\n", new String(message.getPayload()));    
	}
	
	@Test
	void analyzerTestException() {
		/* logDto is valid and exception */
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
	    sendLogDto(logDto);
	    Message<byte[]> message = consumer.receive(0, bindingNameExeptions);
	    assertNotNull(message);
	    message = consumer.receive(0,bindingNameLogs);
	    assertNotNull(message);
	    LOG.debug("\n log receved in consumer {}\n", new String(message.getPayload()));    
	}
	
	
	@Test
	void testDateNull() {
		/* logDto without date*/
		LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 0, "");
		sendLogDto(logDto);
		testLogDtoNotNormal();

	}

	@Test
	void testArtifactIsempty() {
		/* logDto with empty artifact*/
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
		List<LogDoc> logDocs = logsRepo.findAll();
		assertEquals(1, logDocs.size());
		LogDto actualLog = logDocs.get(0).getLogDto();
		assertNotEquals(null, actualLog.dateTime);
		assertEquals(LogType.BAD_REQUEST_EXCEPTION, actualLog.logType);
		assertEquals(LogsAnalyzerAppl.class.getName(), actualLog.artifact);
		assertEquals(0, actualLog.responseTime);
		assertFalse(actualLog.result.isEmpty());
		try {
			Message<byte[]> exceptionMessage = consumer.receive(Long.MAX_VALUE, bindingNameExeptions);   // Long.MAX -> await
			assertNotNull(exceptionMessage);
			LOG.debug("\n exception log: {};\n sent to binding name:{}\n", new String(exceptionMessage.getPayload()),
					bindingNameExeptions);
		}catch (Exception e) {
			fail("No sent exception log to binding name" + bindingNameLogs);
		}
	}

	private void sendLogDto(LogDto logDto) {
		producer.send(new GenericMessage<LogDto>(logDto));
	}
}