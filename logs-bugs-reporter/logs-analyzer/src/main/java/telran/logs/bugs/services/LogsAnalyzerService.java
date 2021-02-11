package telran.logs.bugs.services;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;
import javax.validation.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Service
public class LogsAnalyzerService {
	//consumer with producer functionality
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerService.class);
	@Value("${app-binding-name-exceptions:exceptions-out-0}")
	String bindingNameExceptions;
	@Value("${app-logs-provider-artifact:logs-provider}")
	String logsProviderArtifact;
	@Value("${app-binding-name-logs:log-out-0}")
	String bindingNameLogs;
	@Autowired
	StreamBridge streamBridge;
	@Autowired
	Validator validator;
	
	@Bean
	Consumer<LogDto> getAnalyzerBean(){
		return this::analyzerMethod;
	}
	
    void analyzerMethod(LogDto logDto) {
    	LOG.debug("\n recieved log {}\n", logDto);
    	Set<ConstraintViolation<LogDto>> setViolation = validator.validate(logDto);
    	final LogDto errorLog = logDto;
    	if(!setViolation.isEmpty()){
    		setViolation.forEach(violation -> LOG.error("\n massages: {};\n artifact: {};\n log dto: {}\n",
    			violation.getMessage(), violation.getPropertyPath(), errorLog));
    		logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, logsProviderArtifact, 0, setViolation.toString()); 		
    	} 	 
    	 if(logDto.logType != LogType.NO_EXCEPTION) {
     		streamBridge.send(bindingNameExceptions, logDto);
     		LOG.debug("\n exeption log sent: {};\n to binding name exceptions: {}\n", logDto, bindingNameExceptions);	
     	}
        	streamBridge.send(bindingNameLogs, logDto);
        	LOG.debug("\n log sent: {};\n to binding name logs: {}\n", logDto, bindingNameLogs);
    	    	
	}
}