package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
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

	@Autowired
	InputDestination input;
	@Autowired
	LogsRepo logs;

	@BeforeEach
	void setup(){
		logs.deleteAll();
	}

	@Test
	void docStoreTest() {
//		logs.deleteAll();
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");
		sendingLogDto(logDto);
		LogDoc actualDoc = logs.findAll().get(0);
		assertEquals(logDto, actualDoc.getLogDto());
	}
	@Test
	void testDateNull() {
       LogDto logDto = new LogDto(null, LogType.NO_EXCEPTION, "artifact", 0, "result");
       sendingLogDto(logDto);
	   assertTrue(logs.findAll().isEmpty());
	
	}
	@Test
	void testArtifactIsempty() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, null, 0, "result");
		sendingLogDto(logDto);
		assertTrue(logs.findAll().isEmpty());
	}
	@Test
	void testLogTypeIsNull() {
		LogDto logDto = new LogDto(new Date(), null, "artifact", 0, "");
		sendingLogDto(logDto);
		assertTrue(logs.findAll().isEmpty());
	}
	
	private void sendingLogDto(LogDto logDto) {

		input.send(new GenericMessage<LogDto>(logDto));
		
//		(new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result")));
		//TODO testing of saving LogDto into MongoDb
		
	}

}
