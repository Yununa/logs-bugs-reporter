package telran.logs.bugs;
import static org.junit.jupiter.api.Assertions.*;
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
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class RandomLogsTest {

	private static final long N_LOGS = 100000;
	private static final String CLASS_ARTIFACT = "class";
	private static final String AUTHORIZATION_ARTIFACT = "authorization";
	private static final String AUTHENTICATION_ARTIFACT = "authentication";
	private static final int N_LOGS_SENT = 10;
	

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
				assertEquals(CLASS_ARTIFACT, val);
				break;
			}
			default:
				assertEquals(CLASS_ARTIFACT, val);
				break;
			}
		});
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
		List<LogDto> logs = Stream.generate(() -> randomLogs.createRandomLog()).limit(N_LOGS)
				.collect(Collectors.toList());
		testContent(logs);
		
		Map<LogType, Long> logTypeOccurrencis = logs.stream().collect(Collectors.groupingBy(log -> 
			log.logType, Collectors.counting()
		));
		logTypeOccurrencis.forEach((key, val) ->{
			System.out.printf("LogType: %s occurrences: %d\n", key, val);
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
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertFalse(0 > log.responseTime);
				assertTrue(0 < log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			}
			case NOT_FOUND_EXCEPTION: {
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertEquals("", log.result);
				break;
			}
			default:
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			}
		});
	}
	@Test
	void sendRandomLogs() throws InterruptedException {
		Set<String> logsSet = new HashSet<String>();
		for (int i = 0; i < N_LOGS_SENT; i++) {
			byte[] messageBytes = output.receive(1500).getPayload();
			String messageStr = new String (messageBytes);
			logsSet.add(messageStr);
			System.out.println(messageStr);
		}
		assertEquals(10, logsSet.size());
	}
}