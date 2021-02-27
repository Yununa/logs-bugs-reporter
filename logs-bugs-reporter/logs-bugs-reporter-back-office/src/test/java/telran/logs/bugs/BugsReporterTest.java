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
	private static final LocalDate DATE_OPEN_TEST = LocalDate.of(2020,12,1);
	private static final @NotEmpty String DESCRIPTION_TEST = "Found bug";
	BugDto bugDto = new BugDto(Seriousness.BLOCKING,DESCRIPTION_TEST, DATE_OPEN_TEST);
	BugResponseDto expectBugDto = new BugResponseDto(Seriousness.BLOCKING,DESCRIPTION_TEST, DATE_OPEN_TEST, 0, 1, null,
			BugStatus.OPENED, OpeningMethod.MANUAL);
	
	BugResponseDto expectBugAssign1 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE,
			DATE_OPEN_TEST, ID_VALUE, 1,null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	
	BugAssignDto bugAssign = new BugAssignDto(Seriousness.BLOCKING,DESCRIPTION_TEST, DATE_OPEN_TEST,ID_VALUE);
	BugResponseDto expectBugAssign2 = new BugResponseDto(Seriousness.BLOCKING,DESCRIPTION_TEST, DATE_OPEN_TEST, ID_VALUE, 2, null ,
			BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign3 = new BugResponseDto(Seriousness.BLOCKING,DESCRIPTION_TEST, DATE_OPEN_TEST, ID_VALUE, 3, null ,
			BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign4 = new BugResponseDto(Seriousness.BLOCKING,DESCRIPTION_TEST, DATE_OPEN_TEST, ID_VALUE, 4, null ,
			BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	
	List<BugResponseDto> expectedBug = Arrays.asList(expectBugAssign1, expectBugAssign2, expectBugAssign3, expectBugAssign4);
	
	@Test
	@Order(1)
	void addProgrammerTest() {
		ProgrammerDto programmer = new ProgrammerDto(ID_VALUE, PROGRAMMER_N, EMAIL_TEST);
		testClient.post().uri(BUGS_PROGRAMMERS).contentType(MediaType.APPLICATION_JSON).bodyValue(programmer)
		.exchange().expectStatus().isOk().expectBody(ProgrammerDto.class);
		
	}
	
	@Test
	@Order(2)
	void openBugTest() {	
		testClient.post().uri(BUGS_OPEN).contentType(MediaType.APPLICATION_JSON).bodyValue(bugDto)
		.exchange().expectStatus().isOk().expectBody(BugResponseDto.class).isEqualTo(expectBugDto);
	}
	
	@Test
	@Order(3)
	void openAndAssignTest() {
		openAndAssign(bugAssign, expectBugAssign2);
		openAndAssign(bugAssign, expectBugAssign3);
		openAndAssign(bugAssign, expectBugAssign4);

	}
	
	private void openAndAssign(BugAssignDto bugAssign, BugResponseDto expectBugAssign ) {
		testClient.post().uri(BUGS_OPEN_ASSIGN).bodyValue(bugAssign).exchange().expectStatus().isOk()
		.expectBody( BugResponseDto.class).isEqualTo(expectBugAssign);
		
	}

	@Test
	@Order(4)
	void assignTest() {
		testClient.put().uri(BUGS_ASSIGN).bodyValue(new AssignBugData(1, ID_VALUE, "")).exchange().expectStatus().isOk();
	}
	
	@Test
	@Order(5)
	void getProgrammer() {
		testClient.get().uri(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + ID_VALUE)
		.exchange().expectStatus().isOk().expectBodyList(BugResponseDto.class).isEqualTo(expectedBug);
	}

	@Test
	void invalidAddProgrammerTest() {
		testClient.post().uri(BUGS_PROGRAMMERS)
		.contentType(MediaType.APPLICATION_JSON).bodyValue(new Programmer(1, "Moshe", "GOOGLE"))
		.exchange().expectStatus().isBadRequest();
	}
	
	@Test
	void invalidOpenBugTest() {
		testClient.post().uri(BUGS_OPEN).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(new BugDto(Seriousness.BLOCKING,null, LocalDate.now()))
		.exchange().expectStatus().isBadRequest();
	}
	
	@Test
	void invalidOpenAndAssignTest() {
		testClient.post().uri(BUGS_OPEN_ASSIGN)
		.bodyValue(new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, -1))
		.exchange().expectStatus().isBadRequest();
	}
	
	@Test
	void invalidAssignTest() {
		testClient.put().uri(BUGS_ASSIGN).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(new AssignBugData(0, ID_VALUE, DESCRIPTION_TEST)).exchange().expectStatus().isBadRequest();
	}
	
	@Test
	void invalidGetProgrammerBugTest() {
		testClient.get().uri(BUGS_PROGRAMMERS + "?" + PROGRAMMER + "=" + ID_VALUE)
		.exchange().expectStatus().isBadRequest();
	}
}
