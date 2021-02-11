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
import telran.logs.bugs.LogsAnalyzerAppl;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.mongo.doc.LogsRepo;

@Service
public class LogsAnalyzerService {
	//consumer with producer functionality
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerService.class);
	@Value("${app-binding-name-exeptions:exceptions-out-0}")
	String bindingNameExeptions;
	@Value("${app-binding-name-logs:log-out-0}")
	String bindingNameLogs;
	@Autowired
	StreamBridge streamBridge;
	@Autowired
	LogsRepo logsRepo;
	@Autowired
	Validator validator;
	
	@Bean
	Consumer<LogDto> getAnalyzerBean(){
		return this::analyzerMethod;
	}
    void analyzerMethod(LogDto logDto) {
    	LOG.debug("\n recived log {}\n", logDto);
        if(logDto.logType != null & logDto.logType != LogType.NO_EXCEPTION) {
    		streamBridge.send(bindingNameExeptions, logDto);
    		LOG.debug("\n exeption log sent: {};\n to binding name: {}\n",
    				logDto, bindingNameExeptions);	
    	}
    	Set<ConstraintViolation<LogDto>> setViolation = validator.validate(logDto);
    	if(!setViolation.isEmpty()){
    		setViolation.forEach(violation -> LOG.error("\n massages: {};\n artifact: {};\n log dto: {}\n",
    			violation.getMessage(), violation.getPropertyPath(), logDto));
    		LogDto errorLog = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION,
    				LogsAnalyzerAppl.class.getName(), 0, setViolation.toString()); 	
    		logsRepo.save(new LogDoc(errorLog));
			LOG.debug("\n exception log saved to MongoDB: {}\n", errorLog);
			streamBridge.send(bindingNameExeptions, errorLog);		
    	} 	else {     
        	streamBridge.send(bindingNameLogs, logDto);
        	LOG.debug("\n exception log sent: {};\n to binding name: {}\n", logDto, bindingNameLogs);
    	}
    	
	}
}
