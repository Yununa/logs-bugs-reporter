package telran.logs.bugs;

import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.doc.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	@Autowired
	LogsRepo logs;
	@Autowired
	Validator validator;
	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}
	@Bean
	Consumer<LogDto> getLogDtoconsumer() {
		
			
		return this::takeAndSaveLogDto;
		
	}
	
	void takeAndSaveLogDto(LogDto logDto){	
		Set<ConstraintViolation<LogDto>> setViolation = validator.validate(logDto);
		if(setViolation.isEmpty()) {
			logs.save(new LogDoc(logDto));
		}else {
			setViolation.forEach(violation -> 
			System.out.println(violation.getMessage())
			);	
		}
		System.out.println(logDto);
	}
}
