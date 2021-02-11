package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.doc.LogsRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsDbPopulatorTest {

	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorTest.class);
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
	InputDestination input;
	@Autowired
	OutputDestination output;
	@Autowired
	LogsRepo logs;

	@Test
	void docStoreTest() {
        /* taking and saving to MongoDB logDto */
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
		sendingLogDto(logDto);
		List<LogDoc> logDocs = logs.findAll();
		assertEquals(1, logDocs.size());
		LogDoc actualDoc = logDocs.get(0);
		assertEquals(logDto, actualDoc.getLogDto());
		
	}

	private void sendingLogDto(LogDto logDto) {
		input.send(new GenericMessage<LogDto>(logDto));
	}

}
