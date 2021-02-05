package telran.logs.bugs;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.doc.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);
	}

	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
	StreamBridge streamBridge;
	@Autowired
	LogsRepo logs;
	@Autowired
	Validator validator;

	@Bean
	Consumer<LogDto> getLogDtoconsumer() {
		return this::takeAndSaveLogDto;
	}

	void takeAndSaveLogDto(LogDto logDto) {
		// taking and saving to MongoDB logDto
		LOG.debug("\n recived log {}\n", logDto);
		Set<ConstraintViolation<LogDto>> setViolation = validator.validate(logDto);
		if (setViolation.isEmpty()) {
			logs.save(new LogDoc(logDto));
			LOG.debug("\n log saved to MongoDB: {}\n", logDto);
		} else {
			setViolation.forEach(violation -> LOG.error("\n messages: {};\n artifact: {};\n wrong log dto: {}\n",
					violation.getMessage(), violation.getPropertyPath(), logDto));
			LogDto exceptionLog = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION,
					LogsDbPopulatorAppl.class.getName(), 0, setViolation.toString());
			logs.save(new LogDoc(exceptionLog));
			LOG.debug("\n exception log saved to MongoDB: {}\n", exceptionLog);
			streamBridge.send(bindingName, exceptionLog);
			LOG.debug("\n exception log sent: {};\n to binding name: {}\n", exceptionLog, bindingName);
		}

	}
}
