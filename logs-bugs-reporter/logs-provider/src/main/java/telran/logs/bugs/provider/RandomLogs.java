package telran.logs.bugs;

import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;


@Component 
public class RandomLogs {
	int exceptionProb = 10;
	int secExceptionProb = 30;
	int authenticationProb = 70;
	int nClasses = 20;
	public LogDto createRandomLog(){
		LogDto logDto = new LogDto(null, null, null, authenticationProb, null);
		
		return null;
	}

}
