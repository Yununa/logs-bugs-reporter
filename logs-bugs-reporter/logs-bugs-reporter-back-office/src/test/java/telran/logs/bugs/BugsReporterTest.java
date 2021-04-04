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
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.repo.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureDataJpa
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BugsReporterTest {
	static class EmailBugsCountTest implements EmailBugsCount{
		String email;
		long count;
		public EmailBugsCountTest() {
		}
		public EmailBugsCountTest(String email, long count) {
			super();
			this.email = email;
			this.count = count;
		}
		@Override
		public String getEmail() {
			return email;
		}
		@Override
		public long getCount() {
			return count;
		}
		@Override
		public int hashCode() {
			return Objects.hash(count, email);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EmailBugsCountTest other = (EmailBugsCountTest) obj;
			return count == other.count && Objects.equals(email, other.email);
		}		
	}
	List<EmailBugsCountTest> expectedEmailCounts = Arrays.asList(new EmailBugsCountTest(MOSHE_EMAIL, 4),
			new EmailBugsCountTest(VASYA_EMAIL, 0));
	@Autowired
	WebTestClient testClient;
	@Autowired
	BugRepository bugRepo;
	private static final String PROGRAMMER_MOSHE = "Moshe";
	private static final String PROGRAMMER_VASYA = "Vasya";
	private static final @Min(1) long MOSHE_ID_VALUE = 123;
	private static final @Min(1) long VASYA_ID_VALUE = 125;
	private static final @Email String MOSHE_EMAIL = "moshe@gmail.com";
	private static final LocalDate DATE_OPEN_TEST = LocalDate.of(2021, 1, 1);
	private static final @NotEmpty String DESCRIPTION_TEST = "Found bug";
	private static final @Email String VASYA_EMAIL = "Vasya@gmail.com";
	private static final @NotEmpty String ARTIFACT_ID_TEST = "artifact123";
	private static final @NotEmpty String DESCRIPTION_CLOSE = "Bug was assigned and closed";
	private static final @NotEmpty String TEST_CLOSE_DESCRIPTION = "closed by QA";
	private static final @Min(1) long BUG_CLOSE_ID_VALUE = 2;
	private static final int DAYS = 30;
	BugDto bugDto = new BugDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST);
	BugResponseDto expectBugDto = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, 0, 1, null,
			BugStatus.OPENED, OpeningMethod.MANUAL);
	BugAssignDto bugAssign = new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, MOSHE_ID_VALUE);
	BugResponseDto expectBugAssign1 = new BugResponseDto(Seriousness.BLOCKING,
			DESCRIPTION_TEST + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE, DATE_OPEN_TEST, MOSHE_ID_VALUE, 1, null,
			BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign2 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST,
			MOSHE_ID_VALUE, 2, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign3 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST,
			MOSHE_ID_VALUE, 3, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectBugAssign4 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST,
			MOSHE_ID_VALUE, 4, null, BugStatus.ASSIGNED, OpeningMethod.MANUAL);
	BugResponseDto expectedClosed2 = new BugResponseDto(Seriousness.BLOCKING, DESCRIPTION_CLOSE, DATE_OPEN_TEST,
			MOSHE_ID_VALUE, BUG_CLOSE_ID_VALUE, LocalDate.now(), BugStatus.CLOSED, OpeningMethod.MANUAL);
	
	List<BugResponseDto> expectedBugsToProgrammer = Arrays.asList(expectBugAssign1, expectBugAssign2, expectBugAssign3,
			expectBugAssign4);
	
    List<BugResponseDto> expectedUnAssignedBugs = Arrays.asList(expectBugDto);
    
    List<BugResponseDto> expectedListUnClosed = Arrays.asList(expectBugAssign1, expectBugAssign2, expectBugAssign3, expectBugAssign4);
    
	List<BugResponseDto> expectedBugsAfterColose = Arrays.asList(expectBugAssign1, expectBugAssign2, expectBugAssign3,
			expectBugAssign4);
    List<SeriousnessBugCount> seriousnessBugsDistribution = Arrays.asList(
			new SeriousnessBugCount(Seriousness.BLOCKING, 4),
			new SeriousnessBugCount(Seriousness.CRITICAL, 0),
			new SeriousnessBugCount(Seriousness.MINOR, 0),
			new SeriousnessBugCount(Seriousness.COSMETIC, 0)
			
			);
	List<Seriousness> seriousnessBugsMost = Arrays.asList(Seriousness.BLOCKING);
	
	@Test
	@Order(1)
	void addProgrammerTest() {
		ProgrammerDto programmer = new ProgrammerDto(MOSHE_ID_VALUE, PROGRAMMER_MOSHE, MOSHE_EMAIL);
		addPostRequest(BUGS_PROGRAMMERS, programmer, ProgrammerDto.class);
		programmer = new ProgrammerDto(VASYA_ID_VALUE, PROGRAMMER_VASYA, VASYA_EMAIL);
		addPostRequest(BUGS_PROGRAMMERS, programmer, ProgrammerDto.class);
	}
	
	@Test
	@Order(10)
	void addArtifactTest() {
		ArtifactDto artifact = new ArtifactDto(ARTIFACT_ID_TEST, MOSHE_ID_VALUE);
		addPostRequest(BUGS_ARTIFACTS,artifact,Artifact.class);
		artifact = new ArtifactDto(ARTIFACT_ID_TEST + 1, VASYA_ID_VALUE);
		addPostRequest(BUGS_ARTIFACTS, artifact, Artifact.class);		
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
	void unAssignedBugTest() {
		getRequestList(BUGS_ASSIGNED_NOT,BugResponseDto.class, expectedUnAssignedBugs);
	}
	
	@Test
	@Order(5)
	void assignTest() {
		putRequest(BUGS_ASSIGN, new AssignBugData(1, MOSHE_ID_VALUE, ""));
	}
	
	@Test
	@Order(6)
	void bugsProgrammersBeforeClose() {
		bugsProgrammerTest(BugResponseDto.class, expectedBugsToProgrammer);
	}
	
	@Test
	@Order(7)
	void closeBugTest() {
		putRequest(BUGS_CLOSE, expectedClosed2);
		putRequest(BUGS_CLOSE, expectBugAssign2);
		putRequest(BUGS_CLOSE, new CloseBugData(3, LocalDate.now(), TEST_CLOSE_DESCRIPTION));
	} 

	@Test
	@Order(8)
	void unclosedBugsDurationTest() {
		getRequestList(BUGS_UNCLOSED + "?" + N_DAYS + "=" + DAYS, BugResponseDto.class, expectedListUnClosed);
	}
	
	@Test
	@Order(9)
	void bugsProgrammersAfterClosingTest() {
		bugsProgrammerTest(BugResponseDto.class,expectedBugsAfterColose);
	}
			
	@Test
	void getProgrammerTest() {
		bugsProgrammerTest(BugResponseDto.class,expectedBugsToProgrammer);
	}

	@Test
	void invalidAddProgrammerTest() {
		invalidPostRequest(BUGS_PROGRAMMERS, new Programmer(1, "Moshe", "GOOGLE"));
	}
	
	@Test
	void invalidAddArtifactTest() {
		invalidPostRequest(BUGS_ARTIFACTS, new Artifact());
	}

	@Test
	void invalidOpenBugTest() {
		invalidPostRequest(BUGS_OPEN, new BugDto(Seriousness.BLOCKING, null, LocalDate.now()));
	}

	@Test
	void invalidOpenAndAssignTest() {
		invalidPostRequest(BUGS_OPEN_ASSIGN, new BugAssignDto(Seriousness.BLOCKING, DESCRIPTION_TEST, DATE_OPEN_TEST, -1));
	}

	@Test
	void invalidAssignTest() {
		invalidPutRequest(BUGS_ASSIGN, new AssignBugData(0, MOSHE_ID_VALUE, DESCRIPTION_TEST));
	}

	@Test
	void invalidCloseBugTest() {
		invalidPutRequest(BUGS_CLOSE, new CloseBugData(0, LocalDate.now(), DESCRIPTION_CLOSE));
	}
	
	@Test
	void invalidBugProgrammerId() {
		getRequestList(BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + 1000, BugResponseDto.class, new LinkedList<>());
	}
	
	@Test
	void emailCountsTest() {
		getRequestList(BUGS_PROGRAMMERS_COUNT, EmailBugsCountTest.class, expectedEmailCounts);
		
	}

	@Test
	void programmersMostBugs() {	
		getArrayProgrammers(BUGS_PROGRAMMERS_MOST, new String[] {MOSHE_EMAIL});		
	}
	
	@Test
	void programmersLeastBugs() {	
		getArrayProgrammers(BUGS_PROGRAMMERS_LEAST, new String[] {VASYA_EMAIL, MOSHE_EMAIL});		
	}

	@Test
	void seriousnessDistribution() {
		getRequestList(BUGS_SERIOUSNESS_COUNT, SeriousnessBugCount.class, seriousnessBugsDistribution);
	}
	
	@Test
	void seriousnessMostBugs() {
		getRequestList(BUGS_SERIOUSNESS_MOST, Seriousness.class, seriousnessBugsMost);
	}

	@Test
	void invalidSeriousnessMostBugs() {
		invalidGetRequest(BUGS_SERIOUSNESS_MOST + "?" + N_TYPES + "=" + "-1");
	}	

	@Test
	void invalidOpenAndAssign() {
		BugAssignDto invalidBugAssignDto = new BugAssignDto(Seriousness.BLOCKING,
						DESCRIPTION_TEST, null, 100000);
		notFoundPostRequest( BUGS_OPEN_ASSIGN, invalidBugAssignDto);
	}
	
	
	
	@Test
	void addExistingArtifact() {
		ArtifactDto artifact = new ArtifactDto(ARTIFACT_ID_TEST, MOSHE_ID_VALUE);
		postClientError(BUGS_ARTIFACTS, artifact);
	}
	
	

	@Test 
	void addArtifactNoProgrammer(){
		notFoundPostRequest(BUGS_ARTIFACTS, new ArtifactDto(ARTIFACT_ID_TEST + 1000, 200050));
	}
	
	@Test
	void assignBugNoProgrammer() {
		notFoundPutRequest(BUGS_ASSIGN, new AssignBugData(1, 30000 , DESCRIPTION_TEST));
	}
	
	@Test
	void assignBugNoBug() {
		notFoundPutRequest(BUGS_ASSIGN, new AssignBugData(20000, MOSHE_ID_VALUE, DESCRIPTION_TEST));
	}
	
	@Test
	void closeBugNoBug() {
		notFoundPutRequest(BUGS_CLOSE, new CloseBugData(1000000, LocalDate.now(), DESCRIPTION_CLOSE));
	}
	
	@Test
	void addExistingProgrammer() {
		ProgrammerDto programmer = new ProgrammerDto(MOSHE_ID_VALUE, PROGRAMMER_MOSHE, MOSHE_EMAIL);
		postClientError(BUGS_PROGRAMMERS, programmer);
		
	}
	
	@Test
	void addExistingEmail() {

		postServerError(BUGS_PROGRAMMERS, new ProgrammerDto(400000, "Vasya", MOSHE_EMAIL));

	}

	private void postClientError(String uriStr, Object bodyValue) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(bodyValue).exchange().expectStatus().is4xxClientError();
	}

	private void postServerError(String uriStr, Object bodyValue) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON)
		.bodyValue(bodyValue).exchange().expectStatus().is5xxServerError();
		
	}
	
	private void notFoundPutRequest( String uriStr, Object bodyValue) {
		testClient.put().uri(uriStr)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(bodyValue).exchange().expectStatus().isNotFound();
	}
	
	private void notFoundPostRequest( String uriStr, Object bodyValue) {
		testClient.post().uri(uriStr)
		.contentType(MediaType.APPLICATION_JSON)
		.bodyValue(bodyValue).exchange().expectStatus().isNotFound();
	}
	
	private <T> void addPostRequest(String uriStr, Object bodyValue, Class<T> clazz) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(bodyValue).exchange()
		.expectStatus().isOk().expectBody(clazz);	
	}

	private void postRequest(String uriStr, Object bodyValue, BugResponseDto expectValue) {
		testClient.post().uri(uriStr).bodyValue(bodyValue).exchange().expectStatus().isOk()
				.expectBody(BugResponseDto.class).isEqualTo(expectValue);
	}

	private void putRequest(String uriStr, Object bodyValue) {
		testClient.put().uri(uriStr).bodyValue(bodyValue).exchange().expectStatus().isOk();
	}

	private<T> void getRequestList(String uriId,Class<T> clazz, List<T> expectedList) {
		testClient.get().uri(uriId).exchange().expectStatus().isOk().expectBodyList(clazz).isEqualTo(expectedList);
	}
	private <T> void bugsProgrammerTest(Class<T> clazz, List<T> expectedList) {
		String uriStr = BUGS_PROGRAMMERS + "?" + PROGRAMMER_ID + "=" + MOSHE_ID_VALUE;
		getRequestList( uriStr, clazz, expectedList);
	}
	
	private void invalidPostRequest(String uriStr, Object invalidBodyValue) {
		testClient.post().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidBodyValue).exchange()
				.expectStatus().isBadRequest();
	}

	private void invalidPutRequest(String uriStr, Object invalidBodyValue) {
		testClient.put().uri(uriStr).contentType(MediaType.APPLICATION_JSON).bodyValue(invalidBodyValue).exchange()
				.expectStatus().isBadRequest();
	}
	
	private void invalidGetRequest(String uriStr) {
		testClient.get().uri(uriStr).exchange().expectStatus().isBadRequest();
	}
	
	private void getArrayProgrammers(String uriStr, String[] expected) {
		testClient.get().uri(uriStr).exchange().expectStatus().isOk()
		.expectBody(String[].class).isEqualTo(expected);
	}
}
