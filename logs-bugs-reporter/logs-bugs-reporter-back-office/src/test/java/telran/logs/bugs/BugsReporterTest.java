package telran.logs.bugs;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static telran.logs.bugs.api.DtoConstants.*;
import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BugsReporterTest {
	@Autowired
	WebTestClient testClient;
	@Autowired
	BugRepository bugRepo;
	private static final String PROGRAMMER_N = "Moshe";
	private static final @Min(1) long ID_VALUE = 123;
	private static final @Email String EMAIL_TEST = "moshe@gmail.com";
	private static final LocalDate DATE_OPEN_TEST = LocalDate.of(2020, 12, 1);
	private static final @NotEmpty String DESCRIPTION_TEST = "Found bug";
	BugDto bugDto = new BugDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST);
	BugResponseDto expectBugDto = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, 0, 1, null,
			BugStatus.OPENED, OpeningMethod.MANUAL);

	BugResponseDto expectBugAssign1 = new BugResponseDto(Seriousness.BLOCKING,
			DESCRIPTION_TEST + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE, DATE_OPEN_TEST, ID_VALUE, 1, null,
			BugStatus.ASSIGNED, OpeningMethod.MANUAL);

	BugAssignDto bugAssign = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, ID_VALUE);
	BugResponseDto expectBugAssign2 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST,
			ID_VALUE, 2, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign3 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST,
			ID_VALUE, 3, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign4 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST,
			ID_VALUE, 4, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);

	List<BugResponseDto> expectedBug = Arrays.asList(expectBugAssign1, expectBugAssign2, expectBugAssign3,
			expectBugAssign4);

	@Test
	@Order(1)
	void addProgrammerTest() {
		ProgrammerDto programmer = new ProgrammerDto(ID_VALUE, PROGRAMMER_N, EMAIL_TEST);
		addProgrammer(BUGS_PROGRAMMERS, programmer);
	}

	@Test
	@Order(2)
	void openBugTest() {
		postRequest(BUGS_OPEN, bugDto, expectBugDto);
	}

	@Test
	@Order(3)
	void openAndAssignTest() {
		postRequest(BUGS_OPEN_ASSIGN, bugAssign, expectBugAssign2);
		postRequest(BUGS_OPEN_ASSIGN, bugAssign, expectBugAssign3);
		postRequest(BUGS_OPEN_ASSIGN, bugAssign, expectBugAssign4);
	}

	@Test
	@Order(4)
	void assignTest() {
		putRequest(BUGS_ASSIGN, new AssignBugData(1, ID_VALUE, ""));
	}

	@Test
	@Order(5)
	void getProgrammer() {
		getRequest(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + ID_VALUE, BugResponseDto.class, expectedBug);
	}

	@Test
	void invalidAddProgrammerTest() {
		invalidPost(BUGS_PROGRAMMERS, new Programmer(1, "Moshe", "GOOGLE"));
	}

	@Test
	void invalidOpenBugTest() {
		invalidPost(BUGS_OPEN, new BugDto(Seriousness.BLOCKING, null, LocalDate.now()));
	}

	@Test
	void invalidOpenAndAssignTest() {
		invalidPost(BUGS_OPEN_ASSIGN, new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, -1));
	}

	@Test
	void invalidAssignTest() {
		invalidPutRequest(BUGS_ASSIGN, new AssignBugData(0, ID_VALUE, DESCRIPTION_TEST));
	}

	@Test
	void invalidBugProgrammerId() {
		getRequest(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + 1000, BugResponseDto.class, new LinkedList<>());

	}

	private void addProgrammer(String uriStr, Object bodyValue) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(bodyValue).exchange()
				.expectStatus().isOk().expectBody(Programmer.class);
	}

	private void postRequest(String uriStr, Object bodyValue, BugResponseDto expectValue) {
		testClient.post().uri(uriStr).bodyValue(bodyValue).exchange().expectStatus().isOk()
				.expectBody(BugResponseDto.class).isEqualTo(expectValue);

	}

	private void putRequest(String uriStr, Object bodyValue) {
		testClient.put().uri(uriStr).bodyValue(bodyValue).exchange().expectStatus().isOk();
	}

	private <T> void getRequest(String uriId, Class<T> clazz, List<T> linkedList) {
		testClient.get().uri(uriId).exchange().expectStatus().isOk().expectBodyList(clazz).isEqualTo(linkedList);

	}

	private void invalidPost(String uriStr, Object invalidBodyValue) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidBodyValue).exchange()
				.expectStatus().isBadRequest();

	}

	private void invalidPutRequest(String uriStr, Object invalidBodyValue) {
		testClient.put().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidBodyValue).exchange()
				.expectStatus().isBadRequest();

	}
}
