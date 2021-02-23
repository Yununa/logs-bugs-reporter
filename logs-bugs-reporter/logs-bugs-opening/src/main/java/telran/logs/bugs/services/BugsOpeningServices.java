package telran.logs.bugs.services;

import java.time.LocalDate;
import java.util.function.Consumer;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.*;
import telran.logs.bugs.repository.*;

@Service
public class BugsOpeningServices {
	static Logger LOG = LoggerFactory.getLogger(BugsOpeningServices.class);
	@Autowired
	BugsRepo bugsRepo;
	@Autowired
	ArtifactsRepo artifactsRepo;
	@Autowired
	ProgrammersRepo programmersRepo;

	@Bean
	Consumer<LogDto> getOpeningBugsBean() {
		// consuming all exception logs
		return this::bugsOpening;
	}

	@Transactional
	void bugsOpening(LogDto exceptionLog) {
		LOG.debug("\n Bug opening service recieved exception log:\n {}\n", exceptionLog);
		Programmer programmer = getProgrammer(exceptionLog.artifact);
		Bug bug = new Bug(getDescription(exceptionLog), LocalDate.now(), null, getBugStatus(programmer),
				getSeriousness(exceptionLog.logType), OpeningMethod.AUTHOMATIC, programmer);
		bugsRepo.save(bug);
		LOG.debug("\n Bug opening service has added bug:\n description: {};\n bug status: {};\n "
				+ "seriousness: {};\n assigned to programmer: {}\n",
				getDescription(exceptionLog), getBugStatus(programmer),
				getSeriousness(exceptionLog.logType), getProgrammerName(programmer));

	}

	private String getProgrammerName(Programmer programmer) {
		if (programmer == null) {
			return "not yet defined";
		}
		return programmer.getName();
	}

	private Programmer getProgrammer(@NotEmpty String artifact) {
		Artifact artifactToProgrammer = artifactsRepo.findById(artifact).orElse(null);
		if (artifactToProgrammer != null) {
			return artifactToProgrammer.getProgrammer();
		}
		return null;
	}

	private Seriousness getSeriousness(@NotNull LogType logType) {
		switch (logType) {
		case AUTHENTICATION_EXCEPTION:
			return Seriousness.BLOCKING;
		case AUTHORIZATION_EXCEPTION:
			return Seriousness.CRITICAL;
		case SERVER_EXCEPTION:
			return Seriousness.CRITICAL;
		default:
			return Seriousness.MINOR;
		}

	}

	private BugStatus getBugStatus(Programmer programmer) {
		if (programmer == null) {
			return BugStatus.OPENNED;
		} else
			return BugStatus.ASSIGNED;
	}

	private String getDescription(LogDto exceptionLog) {

		return exceptionLog.logType + " " + exceptionLog.result;
	}

}
