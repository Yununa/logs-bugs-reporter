package telran.logs.bugs;

import java.util.function.Consumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;

@SpringBootApplication
public class LogsDbPopulatorAppl {

	public static void main(String[] args) {
		SpringApplication.run(LogsDbPopulatorAppl.class, args);

	}
	@Bean
	Consumer<LogDto> getLogDtoconsumer() {
		
			
		return this::takeAndSaveLogDto;
		
	}void takeAndSaveLogDto(LogDto logDto){
		//TODO creat LogDocument with validation and saving to MongoDb
		System.out.println(logDto);
	}
	

}
