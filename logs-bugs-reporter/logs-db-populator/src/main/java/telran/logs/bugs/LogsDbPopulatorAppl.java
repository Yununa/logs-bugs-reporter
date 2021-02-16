package telran.logs.bugs;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.doc.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);
	}

	@Autowired
	LogsRepo logs;

	@Bean
	Consumer<LogDto> getLogDtoconsumer() {
		return this::takeAndSaveLogDto;
	}

	void takeAndSaveLogDto(LogDto logDto) {
		// taking and saving to MongoDB logDto
		LOG.debug("\nrecived log {}\n", logDto);
		LOG.debug("\ntaking and saving to MongoDB logDto\n");
		logs.save(new LogDoc(logDto)).subscribe(log -> LOG.debug("\nlog saved to MongoDB: {}\n", log.getLogDto()));
	}
}
