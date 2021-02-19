package telran.logs.bugs.random;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Component 
public class RandomLogs {
	@Value("${app-exeption-prob:10}")
	int exceptionProb;
	@Value("${app-sec-exeption-prob:30}")
	int secExceptionProb;
	@Value("${app-authentication-prob:70}")
	int authenticationProb;
	@Value("${app-count-classes:20}")
	int nClasses;
	@Value("${app-min-random-value:1}")
	int minRandomValue;
	@Value("${app-max-random-value:101}")
	int maxRandomValue;
	@Value("${app-min-response-time-value:20}")
	int minResponseTimeValue;
	@Value("${app-max-response-time-value:200}")
	int maxResponseTimeValue;
	
	public int getnClasses() {
		return nClasses;
	}

	public int getExceptionProb() {
		return exceptionProb;
	}

	public int getSecExceptionProb() {
		return secExceptionProb;
	}

	public int getAuthenticationProb() {
		return authenticationProb;
	}

	public int getMinRandomValue() {
		return minRandomValue;
	}

	public int getMaxRandomValue() {
		return maxRandomValue;
	}

	public int getMinResponseTimeValue() {
		return minResponseTimeValue;
	}

	public int getMaxResponseTimeValue() {
		return maxResponseTimeValue;
	}

	public LogDto createRandomLog(){
		LogType logType = getLogType();
		return new LogDto(new Date(), logType, getArtifact(logType),getResponseTime(logType), ""); 
	}
	
	private int getResponseTime(LogType logType) {
		if(logType == LogType.NO_EXCEPTION) {
			return ThreadLocalRandom.current().nextInt(minRandomValue, maxResponseTimeValue);
		}else {
			return 0;
		}
	}
	
	private String getArtifact(LogType logType) {
		EnumMap<LogType, String> artifactMap = getArtifactMap();
		return artifactMap.get(logType);
	}
	
	private EnumMap<LogType, String> getArtifactMap() {
		EnumMap<LogType, String> res = new  EnumMap<>(LogType.class);
		Arrays.asList(LogType.values()).forEach(logType ->{
		fillArtifactMap(res, logType);
		});
		return res;
	}
	
	private void fillArtifactMap(EnumMap<LogType, String> artifactMap, LogType logType) {
		switch(logType) {
		case NO_EXCEPTION:{
			artifactMap.put(LogType. NO_EXCEPTION,  getRamdomClass());
			break;
		}
		case  NOT_FOUND_EXCEPTION:{
			artifactMap.put(LogType. NOT_FOUND_EXCEPTION, getRamdomClass());
			break;
		}	
		case BAD_REQUEST_EXCEPTION:{
			artifactMap.put(LogType.BAD_REQUEST_EXCEPTION, getRamdomClass());
			break;
		}		
		case DUPLICATED_KEY_EXCEPTION:{
			artifactMap.put(LogType.DUPLICATED_KEY_EXCEPTION, getRamdomClass());
			break;
		}						
		case SERVER_EXCEPTION:{
			artifactMap.put(LogType. SERVER_EXCEPTION, getRamdomClass());
			break;
		}		
		case AUTHORIZATION_EXCEPTION:{
			artifactMap.put(LogType.AUTHORIZATION_EXCEPTION, "authorization");
			break;
		}
		case AUTHENTICATION_EXCEPTION:{
			artifactMap.put(LogType.AUTHENTICATION_EXCEPTION, "authentication");
			break;
		}			
	}	
}
	
	private String getRamdomClass() {
		
		return "class" + ThreadLocalRandom.current().nextInt(minRandomValue, nClasses + 1);
	}

	private LogType getLogType() {
		if(getRandomNumber() <= exceptionProb) {
			return getSecurityException();
			
		}else {
			return LogType.NO_EXCEPTION;
		}
	}
	
	private LogType getSecurityException() {
		if(getRandomNumber() <= secExceptionProb) {
			return getAuthenticationExeption();
		}else {
			return getNonSecurityExeption();
			}
	}
	
	private LogType getNonSecurityExeption() {
		LogType nonSecurityExeption[] = { LogType.BAD_REQUEST_EXCEPTION, LogType.DUPLICATED_KEY_EXCEPTION,
				LogType.NOT_FOUND_EXCEPTION, LogType.SERVER_EXCEPTION};
		int length = nonSecurityExeption.length;
		return nonSecurityExeption[ThreadLocalRandom.current().nextInt(0, length)];
	}
	
	private LogType getAuthenticationExeption() {
		if(getRandomNumber() <= authenticationProb) {
			return LogType.AUTHENTICATION_EXCEPTION;
		}else {
			return LogType.AUTHORIZATION_EXCEPTION;
		}
	}
	
	private int getRandomNumber() {
		return ThreadLocalRandom.current().nextInt(minRandomValue, maxRandomValue);
	}
}