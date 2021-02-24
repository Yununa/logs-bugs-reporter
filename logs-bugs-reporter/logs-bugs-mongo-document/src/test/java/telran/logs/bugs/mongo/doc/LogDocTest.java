package telran.logs.bugs.mongo.doc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import telran.logs.bugs.dto.*;
import java.util.Date;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static telran.logs.bugs.api.DtoConstants.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=LogsRepo.class)
@EnableAutoConfiguration
public class LogDocTest {
	@Autowired
	LogsRepo logs;
	@Test
	void docStoreTest() {
		LogDto logDto = new LogDto(new Date(),LogType.NO_EXCEPTION, ARTIFACT,
				20, RESULT);
		LogDto logDto1 = new LogDto(new Date(),LogType.NO_EXCEPTION, ARTIFACT,
				25, RESULT);
		logs.save(new LogDoc(logDto)).block();
		LogDoc actualDoc = logs.findAll().blockFirst();
		assertEquals(logDto, actualDoc.getLogDto());
		assertNotEquals(logDto1, actualDoc.getLogDto());
	}
	
}
