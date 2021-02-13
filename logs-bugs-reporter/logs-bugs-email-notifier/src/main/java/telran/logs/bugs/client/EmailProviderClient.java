package telran.logs.bugs.client;

import org.springframework.stereotype.Component;

@Component
public class EmailProviderClient {
	public String getEmailByArtifact(String artifact) {
		//TODO communication with sync service for getting programmer's email
		return null;
	}
	public String getAssignerMail() {
		//TODO communicating with sync service for getting assigner’s email
		return null;
	}
}