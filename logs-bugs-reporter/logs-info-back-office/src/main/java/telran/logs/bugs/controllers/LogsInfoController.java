package telran.logs.bugs.controllers;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.LogsInfo;
import static telran.logs.bugs.api.DtoConstants.*;

import java.util.List;

import javax.validation.constraints.Min;

@RestController
@Validated
public class LogsInfoController {

	static Logger LOG = LoggerFactory.getLogger(LogsInfoController.class);
	@Autowired
	LogsInfo logsInfo;
	@GetMapping(value = LOGS_URI,produces=APPL_STREAM_JSON)
	Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		LOG.debug("Logs sent to a client");
		return result;
	}
	@GetMapping(value = LOGS_TYPE,produces=APPL_STREAM_JSON)
	Flux<LogDto> getLogsByTypes(@RequestParam(name = TYPE) LogType logType) {
		Flux<LogDto> result = logsInfo.getLogsType(logType);
		LOG.debug("Logs of type {} sent to a client", logType);
		return result;
	}
	@GetMapping(value = LOGS_EXCEPTIONS)
	Flux<LogDto> getAllExceptions() {
		Flux<LogDto> result = logsInfo.getAllExceptions();
		LOG.debug("Logs Exceptions sent to a client");
		return result;
	}
	@GetMapping(value = LOGS_DISTRIBUTION_TYPE)
	Flux<LogTypeCount> getLogTypeOccurrences(){
		return logsInfo.getLogTypeOccurrences();
	}
	@GetMapping(value = LOGS_ENCOUNTERED_EXCPTIONS)
	Flux<LogType> getMostEncountedLogExceptions(@RequestParam(name = AMOUNT, defaultValue = "2")@Min(1) int nExceptions){
		return logsInfo.getMostEncounteredExceptionTypes(nExceptions);
	}
	@GetMapping(value = LOGS_DISTRIBUTION_ARTIFACT)
	Flux<ArtifactCount> getArtifactOccurrences(){
		return logsInfo.getArtifactOccurrences();
	}
	@GetMapping(value = LOGS_ENCOUNTERED_ARTIFACTS)
	Mono<List<String>> getMostEncounteredArtifacts(@RequestParam(name = AMOUNT, defaultValue = "2")@Min(1) int nArtifacts){
		return logsInfo.getMostEncounteredArtifacts(nArtifacts).collectList();
	}

}
