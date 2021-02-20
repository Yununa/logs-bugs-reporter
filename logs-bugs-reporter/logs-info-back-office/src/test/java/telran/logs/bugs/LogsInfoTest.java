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
	static List<LogDto> exceptionLogs;
	static Date DATE = new Date();
	static final String ARTIFACT = "artifact";
	static final String RESULT = "result";
	private static final String AUTHENTICATION_ERROR = "authentication error";
	private static final String AUTHORIZATION_ERROR = "authorization error";
	private static final String BAD_REQUEST_ERROR = "bad request error";
	
	@BeforeEach
	void setUp() {
		exceptionLogs = new ArrayList<>(Arrays.asList(
				new LogDto(DATE, LogType.AUTHENTICATION_EXCEPTION, ARTIFACT, 0, AUTHENTICATION_ERROR),
				new LogDto(DATE, LogType.AUTHORIZATION_EXCEPTION, ARTIFACT, 0, AUTHORIZATION_ERROR),
				new LogDto(DATE, LogType.BAD_REQUEST_EXCEPTION, ARTIFACT, 0, BAD_REQUEST_ERROR)
				));
		noExceptionLogs = new ArrayList<>(Arrays.asList(
				new LogDto(DATE, LogType.NO_EXCEPTION, ARTIFACT, 20, RESULT),
				new LogDto(DATE, LogType.NO_EXCEPTION, ARTIFACT, 25, RESULT),
				new LogDto(DATE, LogType.NO_EXCEPTION, ARTIFACT, 30, RESULT)
				));
		allLogs = new ArrayList<>(noExceptionLogs);
		allLogs.addAll(exceptionLogs);
		
	}
	
	@Test
	@Order(1)
	void allLogsTest() {
		getAllLogs();
		webClient.get().uri("/logs")
		.exchange().expectStatus().isOk()
		.expectBodyList(LogDto.class).isEqualTo(allLogs);
		
	}

	private void getAllLogs() {
		Flux<LogDoc> logsFlux = logRepo.saveAll(allLogs.stream().map(LogDoc::new).collect(Collectors.toList()));
		logsFlux.buffer().blockFirst();
		
	}

	@Test
	void exceptionLogsTest() {
		webClient.get().uri("/logs/exceptions")
		.exchange().expectStatus().isOk()
		.expectBodyList(LogDto.class).isEqualTo(exceptionLogs);
	}
	
	@Test
	void noExceptionLogsTest() {
		webClient.get().uri("/logs/type?type=NO_EXCEPTION")
		.exchange().expectStatus().isOk()
		.expectBodyList(LogDto.class).isEqualTo(noExceptionLogs);
	}
	
	@Test
	void badRequestLogsTest() {
		webClient.get().uri("/logs/type?type=EXCEPTION")
		.exchange().expectStatus().isBadRequest();
	}
}
