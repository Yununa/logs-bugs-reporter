package telran.logs.bugs;

import java.time.LocalDate;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;
import telran.logs.bugs.repository.BugsRepo;
import telran.logs.bugs.repository.ProgrammersRepo;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.*;
import static telran.logs.bugs.api.DtoConstants.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(TestChannelBinderConfiguration.class)
public class BugsOpeningTest {

	@Autowired
	ProgrammersRepo progRepo;
	@Autowired
	BugsRepo bugsRepo;
	@Autowired
	InputDestination input;
	Programmer programmer = new Programmer(123, "Moshe", "moshe@gmail.com");

	@Test
	@Sql("programmersAndArtifacts.sql")
	void authenticationTest() {
		LogDto logDtoWithBug = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, BUG_ARTIFACT_NUMBER + "1", 0, ERROR_MESSAGE);
		sendingLogDto(logDtoWithBug);
		Bug bug = new Bug(getDescription(logDtoWithBug), LocalDate.now(), null, BugStatus.ASSIGNED,
				Seriousness.BLOCKING, OpeningMethod.AUTHOMATIC, programmer);
		assertEquals(bug, bugsRepo.findAll().get(0));
	}

	@Test
	@Sql("programmersAndArtifacts.sql")
	void authorizaitionTest() {
		LogDto logDtoWithBug = new LogDto(new Date(), LogType.AUTHORIZATION_EXCEPTION, BUG_ARTIFACT_NUMBER + "1", 0, ERROR_MESSAGE);
		sendingLogDto(logDtoWithBug);
		Bug bug = new Bug(getDescription(logDtoWithBug), LocalDate.now(), null, BugStatus.ASSIGNED,
				Seriousness.CRITICAL, OpeningMethod.AUTHOMATIC, programmer);
		assertEquals(bug, bugsRepo.findAll().get(0));
	}

	@Test
	@Sql("programmersAndArtifacts.sql")
	void noProgrammerServerTest() {
		LogDto logDtoWithBug = new LogDto(new Date(), LogType.SERVER_EXCEPTION, "without bug", 0, ERROR_MESSAGE);
		sendingLogDto(logDtoWithBug);
		Bug bug = new Bug(getDescription(logDtoWithBug), LocalDate.now(), null, BugStatus.OPENNED, Seriousness.CRITICAL,
				OpeningMethod.AUTHOMATIC, null);
		assertEquals(bug, bugsRepo.findAll().get(0));
	}

	@Test
	@Sql("programmersAndArtifacts.sql")
	void noExeptionTest() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, BUG_ARTIFACT_NUMBER + "1", 0, ERROR_MESSAGE);
		sendingLogDto(logDto);
		Bug bug = new Bug(getDescription(logDto), LocalDate.now(), null, BugStatus.ASSIGNED, Seriousness.MINOR,
				OpeningMethod.AUTHOMATIC, programmer);
		assertEquals(bug, bugsRepo.findAll().get(0));
	}

	@Test
	@Sql("programmersAndArtifacts.sql")
	void badRequestNoProgrammerTest() {
		LogDto logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, "without bug", 0, ERROR_MESSAGE);
		sendingLogDto(logDto);
		Bug bug = new Bug(getDescription(logDto), LocalDate.now(), null, BugStatus.OPENNED, Seriousness.MINOR,
				OpeningMethod.AUTHOMATIC, null);
		assertEquals(bug, bugsRepo.findAll().get(0));
	}

	private String getDescription(LogDto logDtoWithBug) {
		return logDtoWithBug.logType + " " + ERROR_MESSAGE;
	}

	private void sendingLogDto(LogDto logDto) {
		input.send(new GenericMessage<LogDto>(logDto));
	}

}
