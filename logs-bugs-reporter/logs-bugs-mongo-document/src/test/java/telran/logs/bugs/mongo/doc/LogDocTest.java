package telran.logs.bugs.mongo.doc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import telran.logs.bugs.dto.*;
import java.util.Date;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=LogsRepo.class)
@EnableAutoConfiguration
public class LogDocTest {
	@Autowired
	LogsRepo logs;
	@Test
	void docStoreTest() {
		LogDto logDto = new LogDto(new Date(),LogType.NO_EXCEPTION, "artifact",
				20,"result");
		logs.save(new LogDoc(logDto));
		LogDoc actualDoc = logs.findAll().get(0);
		assertEquals(logDto, actualDoc.getLogDto());			
	}
	
}
