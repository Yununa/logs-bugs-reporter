package telran.logs.bugs;

import java.util.Date;
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
import org.springframework.cloud.stream.binder.test.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	@Autowired
	InputDestination producer;
	@Autowired
	OutputDestination consumer;
	@Value("${app-binding-name}")
	String bindingName;
	
	@BeforeEach
	void setUp() {
	     consumer.clear();
	}
	
	@Test
	void analyzerTestNonException() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
	    sendLogDto(logDto);
	    assertThrows(Exception.class, consumer::receive);
	    
	}
	
	@Test
	void analyzerTestException() {
		LogDto logDto = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
	    sendLogDto(logDto);
	    Message<byte[]> message = consumer.receive(0, bindingName);
	    assertNotNull(message);
	    LOG.debug("\n log receved in consumer {}\n", new String(message.getPayload()));    
	}
	
	private void sendLogDto(LogDto logDto) {
		producer.send(new GenericMessage<LogDto>(logDto));
	}
}