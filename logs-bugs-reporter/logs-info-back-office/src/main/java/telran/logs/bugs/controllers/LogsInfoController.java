package telran.logs.bugs.controllers;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
public class LogsInfoController {
	private static final String LOGS_DISTRIBUTION = "/logs/distribution";
	private static final String LOGS_EXCEPTIONS = "/logs/exceptions";
	private static final String LOGS_TYPE = "/logs/type";
	private static final String APPL_STREAM_JSON = "application/stream+json";
	private static final String LOGS = "/logs";
	static Logger LOG = LoggerFactory.getLogger(LogsInfoController.class);
	@Autowired
	LogsInfo logsInfo;
	@GetMapping(value = LOGS,produces=APPL_STREAM_JSON)
	Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		LOG.debug("Logs sent to a client");
		return result;
	}
	@GetMapping(value = LOGS_TYPE,produces=APPL_STREAM_JSON)
	Flux<LogDto> getLogsByTypes(@RequestParam(name="type") LogType logType) {
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
	@GetMapping(value = LOGS_DISTRIBUTION)
	Flux<LogTypeCount> getLogTypeOccurrences(){
		return logsInfo.getLogTypeOccurrences();
	}

}
