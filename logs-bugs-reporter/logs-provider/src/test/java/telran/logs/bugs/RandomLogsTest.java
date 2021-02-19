package telran.logs.bugs;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.beans.factory.annotation.Value;
import telran.logs.bugs.random.RandomLogs;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class RandomLogsTest {
	
	static Logger LOG = LoggerFactory.getLogger(RandomLogsTest.class);
	@Value("${app-n-logs-sent:10}")
	int nLogsSent;
	
	@Autowired
	RandomLogs randomLogs;
	@Autowired
	OutputDestination output;

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