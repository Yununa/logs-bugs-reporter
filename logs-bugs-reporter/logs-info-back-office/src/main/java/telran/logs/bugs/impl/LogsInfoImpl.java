package telran.logs.bugs.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.interfaces.LogsInfo;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;

@Service
public class LogsInfoImpl implements LogsInfo {
    @Autowired
	LogRepository logRepository;
	@Override
	public Flux<LogDto> getAllLogs() {
		
		return logRepository.findAll().map(LogDoc::getLogDto);
	}

	@Override
	public Flux<LogDto> getAllExceptions() {

		return logRepository.findByLogTypeNot(LogType.NO_EXCEPTION);
	}

	@Override
	public Flux<LogDto> getLogsType(LogType logType) {
	
		return logRepository.findByLogType(logType);

	}

	@Override
	public Flux<LogTypeCount> getLogTypeOccurrences() {
		
		return logRepository.getLogTypeCounts();
	}

	@Override
	public Flux<LogType> getMostEncounteredExceptionTypes(int nExceptions) {
		Flux<LogTypeCount> logsCount = logRepository.getMostEncounteredExceptionTypes(nExceptions);
		return logsCount.map(t -> t.logType);
	}

	@Override
	public Flux<ArtifactCount> getArtifactOccurrences() {
		
		return logRepository.getArtifactCount();
	}

	@Override
	public Flux<String> getMostEncounteredArtifacts(int nArtifacts) {
		Flux<ArtifactCount> artifactsCount  = logRepository.getMostEncounteredArtifacts(nArtifacts);
		return artifactsCount.map(ac -> ac.artifact);
	}

}
