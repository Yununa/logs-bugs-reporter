package telran.logs.bugs;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import telran.logs.bugs.dto.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.beans.factory.annotation.Value;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class RandomLogsTest {
	
	static Logger LOG = LoggerFactory.getLogger(RandomLogsTest.class);
	@Value("${app-class-artifact:class}")
	String CLASS_ARTIFACT;
	@Value("${app-authorization-artifact:authorization}")
	String AUTHORIZATION_ARTIFACT;
	@Value("${app-authentication-artifact:authentication}")
	String AUTHENTICATION_ARTIFACT;
	@Value("${app-n-logs:100000}")
	long nLogs;
	@Value("${app-n-logs-sent:10}")
	int nLogsSent;
	

	@Autowired
	RandomLogs randomLogs;
	@Autowired
	OutputDestination output;
	@Test
	void testArtifactMap() throws NoSuchMethodException, SecurityException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EnumMap<LogType, String> logArtifactMap = getMap();
		logArtifactMap.forEach((key, val) -> {
			switch (key) {
			case AUTHENTICATION_EXCEPTION: {
				assertEquals(AUTHENTICATION_ARTIFACT, val);
				break;
			}
			case AUTHORIZATION_EXCEPTION:{
				assertEquals(AUTHORIZATION_ARTIFACT, val);
				break;
			}
			case BAD_REQUEST_EXCEPTION:{
				
				getArtifactClassTest(val);
				break;
			}
			default:
				getArtifactClassTest(val);
			}
		});
	}
	
	private void getArtifactClassTest(String artifact) {
		assertEquals(CLASS_ARTIFACT, artifact.substring(0, 5));
		int classNum = Integer.parseInt(artifact.substring(5));
		assertTrue(classNum >=1 && classNum <= randomLogs.nClasses);
		

	}

	private EnumMap<LogType, String> getMap() throws NoSuchMethodException, SecurityException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method getMethod = randomLogs.getClass().getDeclaredMethod("getArtifactMap");
		getMethod.setAccessible(true);
		@SuppressWarnings("unchecked")
		EnumMap<LogType, String> logTypeArtifactMap = (EnumMap<LogType, String>)getMethod
		.invoke(randomLogs);
		return logTypeArtifactMap;
	}
	
	@Test
	void testLogsDto(){
		List<LogDto> logs = Stream.generate(() -> randomLogs.createRandomLog()).limit(nLogs)
				.collect(Collectors.toList());
		testContent(logs);
		
		Map<LogType, Long> logTypeOccurrencis = logs.stream().collect(Collectors.groupingBy(log -> 
			log.logType, Collectors.counting()
		));
		logTypeOccurrencis.forEach((key, val) ->{
			LOG.debug("\n LogType: {};\n occurrences: {}\n", key, val);
		});
		assertEquals(LogType.values().length, logTypeOccurrencis.entrySet().size());
	}
	
	private void testContent(List<LogDto> logs) {
		logs.forEach(log -> {
			switch (log.logType) {
			case AUTHORIZATION_EXCEPTION: {
				assertEquals(AUTHORIZATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertEquals("", log.result);
				break;
			}
			case AUTHENTICATION_EXCEPTION:{
				assertEquals(AUTHENTICATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			}
			case NO_EXCEPTION: {
				getArtifactClassTest(log.artifact);
				assertFalse(0 > log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			}
			case NOT_FOUND_EXCEPTION: {
				getArtifactClassTest(log.artifact);
				assertEquals(0, log.responseTime);
				assertEquals("", log.result);
				break;
			}
			default:
				getArtifactClassTest(log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			}
		});
	}
	@Test
	void sendRandomLogs() throws InterruptedException {
		Set<String> logsSet = new HashSet<String>();
		for (int i = 0; i < nLogsSent; i++) {
			byte[] messageBytes = output.receive(1500).getPayload();
			String messageStr = new String (messageBytes);
			logsSet.add(messageStr);
			LOG.debug("\n recived in test : {}\n" , messageStr);
		}
		assertEquals(nLogsSent, logsSet.size());
	}
}