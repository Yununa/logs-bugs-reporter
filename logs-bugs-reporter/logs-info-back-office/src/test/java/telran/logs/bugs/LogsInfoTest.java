package telran.logs.bugs;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;
import static telran.logs.bugs.api.DtoConstants.*;

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LogsInfoTest {

	@Autowired
	WebTestClient webClient;
	@Autowired
	LogRepository logRepo;
	static List<LogDto> allLogs;
	static List<LogDto> noExceptionLogs;
	static List<LogDto> authorizationExceptions;
	static List<LogDto> authenticationExceptions;
	static List<LogDto> badRequestExceptions;
	static List<LogDto> allExceptions;
	static Date DATE = new Date();


	@BeforeAll
	static void setUp() {
		noExceptionLogs = new ArrayList<>(
				Arrays.asList(new LogDto(DATE, LogType.NO_EXCEPTION, ARTIFACT_NO_EXEPTION, 20, RESULT),
						new LogDto(DATE, LogType.NO_EXCEPTION, ARTIFACT_NO_EXEPTION, 25, RESULT),
						new LogDto(DATE, LogType.NO_EXCEPTION, ARTIFACT_NO_EXEPTION, 30, RESULT)));
		authenticationExceptions = new ArrayList<>(Arrays.asList(
				new LogDto(DATE, LogType.AUTHENTICATION_EXCEPTION, ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR),
				new LogDto(DATE, LogType.AUTHENTICATION_EXCEPTION, ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR),
				new LogDto(DATE, LogType.AUTHENTICATION_EXCEPTION, ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR),
				new LogDto(DATE, LogType.AUTHENTICATION_EXCEPTION, ARTIFACT_AUTHENTICATION, 0, AUTHENTICATION_ERROR)

		));
		authorizationExceptions = new ArrayList<>(Arrays.asList(
				new LogDto(DATE, LogType.AUTHORIZATION_EXCEPTION, ARTIFACT_AUTHORIZATION, 0, AUTHORIZATION_ERROR),
				new LogDto(DATE, LogType.AUTHORIZATION_EXCEPTION, ARTIFACT_AUTHORIZATION, 0, AUTHORIZATION_ERROR)

		));
		badRequestExceptions = new ArrayList<>(Arrays
				.asList(new LogDto(DATE, LogType.BAD_REQUEST_EXCEPTION, ARTIFACT_BAD_REQUEST, 0, BAD_REQUEST_ERROR)

				));

		fillAllExceptions();
		fillAllLogs();

	}

	private static void fillAllLogs() {
		allLogs = new ArrayList<>(noExceptionLogs);
		allLogs.addAll(allExceptions);
	}

	private static void fillAllExceptions() {
		allExceptions = new ArrayList<>(authenticationExceptions);
		allExceptions.addAll(authorizationExceptions);
		allExceptions.addAll(badRequestExceptions);

	}

	@Test
	@Order(1)
	void allLogsTest() {
		getAllLogs();
		webClient.get().uri(LOGS_URI).exchange().expectStatus().isOk().expectBodyList(LogDto.class).isEqualTo(allLogs);

	}

	@Test
	void allExceptionLogsTest() {
		webClient.get().uri(LOGS_EXCEPTIONS).exchange().expectStatus().isOk().expectBodyList(LogDto.class)
				.isEqualTo(allExceptions);
	}

	@Test
	void noExceptionLogsTest() {
		webClient.get().uri(LOGS_TYPE + "?" + TYPE + "=" + LogType.NO_EXCEPTION).exchange().expectStatus().isOk()
				.expectBodyList(LogDto.class).isEqualTo(noExceptionLogs);
	}

	@Test
	void badRequestLogsTest() {
		getBadRequest(LOGS_TYPE, TYPE, "EXCEPTION");
	}

	private void getAllLogs() {
		Flux<LogDoc> logsFlux = logRepo.saveAll(allLogs.stream().map(LogDoc::new).collect(Collectors.toList()));
		logsFlux.buffer().blockFirst();

	}

	private void getBadRequest(String baseStr, String type, String value) {
		webClient.get().uri(baseStr + "?" + type + "=" + value).exchange().expectStatus().isBadRequest();

	}

	@Test
	void logsTypeCountTest() {
		LogTypeCount[] expected = {
				new LogTypeCount(LogType.AUTHENTICATION_EXCEPTION, authenticationExceptions.size()),
				new LogTypeCount(LogType.NO_EXCEPTION, noExceptionLogs.size()),
				new LogTypeCount(LogType.AUTHORIZATION_EXCEPTION, authorizationExceptions.size()),
				new LogTypeCount(LogType.BAD_REQUEST_EXCEPTION, badRequestExceptions.size()) };
		runTest(LOGS_DISTRIBUTION_TYPE, LogTypeCount[].class, expected);
	}

	@Test
	void artifactsCountTest() {
		ArtifactCount[] expected = {
				new ArtifactCount(ARTIFACT_AUTHENTICATION, authenticationExceptions.size()),
				new ArtifactCount(ARTIFACT_NO_EXEPTION, noExceptionLogs.size()),
				new ArtifactCount(ARTIFACT_AUTHORIZATION, authorizationExceptions.size()),
				new ArtifactCount(ARTIFACT_BAD_REQUEST, badRequestExceptions.size()) };
		runTest(LOGS_DISTRIBUTION_ARTIFACT, ArtifactCount[].class, expected);
	}

	@Test
	void mostEncounteredTypesTest() {
		LogType[] expected = {
				LogType.AUTHENTICATION_EXCEPTION, LogType.AUTHORIZATION_EXCEPTION };
		runTest(LOGS_ENCOUNTERED_EXCPTIONS, LogType[].class, expected);
	}

	@Test
	void mostEncounteredArtifacts() {
		String[] expected = {
				ARTIFACT_AUTHENTICATION, ARTIFACT_NO_EXEPTION };
		runTest(LOGS_ENCOUNTERED_ARTIFACTS, String[].class, expected);
	}

	private <T> void runTest(String logsType, Class<T[]> clazz, T[] expected) {
		webClient.get().uri(logsType).exchange().expectStatus().isOk().expectBody(clazz).isEqualTo(expected);
	}

	@Test
	void exceptionsEncounteredBadRequest() {
		getBadRequest(LOGS_ENCOUNTERED_EXCPTIONS, AMOUNT, " ");
	}

	@Test
	void artifactsEnocounteredBadRequest() {
		getBadRequest(LOGS_ENCOUNTERED_ARTIFACTS, AMOUNT, " ");
	}
	@Test
	void badRequestMostArtifacts() {
		String uriStr = LOGS_ENCOUNTERED_ARTIFACTS + "?" + AMOUNT + "=-1";
		badGetRequest(uriStr);
	}
	@Test
	void badRequestMostExceptionTypes() {
		String uriStr = LOGS_ENCOUNTERED_EXCPTIONS + "?" + AMOUNT + "=0";
		badGetRequest(uriStr);
	}
	private void badGetRequest(String uriStr) {
		webClient.get().uri(uriStr)
		.exchange().expectStatus().isBadRequest();
	}

}
