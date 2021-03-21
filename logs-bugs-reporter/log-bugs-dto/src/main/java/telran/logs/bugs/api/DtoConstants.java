package telran.logs.bugs.api;

import javax.validation.constraints.NotEmpty;
import telran.logs.bugs.dto.LogType;

public interface DtoConstants {

	String ID = "id";
	String NAME = "name";
	String TYPE = "type";
	String EMAIL = "email";
	String RESULT = "result";
	String COUNT = "count";
	String AMOUNT = "amount";
	String SERIOUSNESS = "seriousness";
	String N_TYPES = "n_types";

	String OPENING_METHOD = "opening_method";
	String AUTHENTICATION = "authentication";
	String DESCRIPTION = "description";

	// Programmers
	String PROGRAMMER = "programmer";
	String PROGRAMMERS = "programmers";
	String PROGRAMMER_ID = "programmer_id";
	String PROGRAMMER_NAME = "Programmer";
	String N_PROGRAMMERS = "N_PROGRAMMERS";


	// Assigns
	String ASSIGNER_NAME = "Opened Bugs Assigner";

	// Artifacts
	String ARTIFACT = "artifact";
	String ARTIFACTS = "artifacts";
	String ARTIFACT_ID = "artifact_id";
	String ARTIFACT_NAME = "Artifact";

	// Logs
	String LOGS = "logs";
	String LOG_TYPE = "logType";

	// Bugs
	String BUGS = "bugs";
	@NotEmpty
	String BUG_ARTIFACT_NUMBER = "bug";
	String BUG_ID = "bug_id";
	

	// Date
	String DATE_OPEN = "date_open";
	String DATE_CLOSE = "date_close";
	String N_DAYS = "n_days";

	// URI
	String BUGS_PROGRAMMERS = "/bugs/programmers";
	String BUGS_OPEN = "/bugs/open";
	String BUGS_OPEN_ASSIGN = "/bugs/open/assign";
	String BUGS_ASSIGN = "/bugs/assign";
	String BUGS_CLOSE ="/bugs/close";
	String BUGS_UNCLOSED = "/bugs/unclosed";
	String BUGS_PROGRAMMERS_COUNT = "/bugs/programmers/count";
	String BUGS_ARTIFACTS = "/bugs/artifacts";
	String BUGS_ASSIGNED_NOT = "/bugs/assigned/not";
	String BUGS_PROGRAMMERS_MOST = "/bugs/programmers/most";
	String BUGS_PROGRAMMERS_LEAST = "/bugs/programmers/least";
	String BUGS_SERIOUSNESS_COUNT = "/bugs/seriousness/count";
	String BUGS_SERIOUSNESS_MOST = "/bugs/seriousness/most";
	
	String MAIL_ASSIGNER = "/mail/assigner";
	String EMAIL_ARTIFACT = "/email/{artifact}";
	String EMAIL_ARTIFACT_NUMBER = "/email/artifact";
	String LOGS_DISTRIBUTION_TYPE = "/logs/distribution/type";
	String LOGS_EXCEPTIONS = "/logs/exceptions";
	String LOGS_TYPE = "/logs/type";
	String APPL_STREAM_JSON = "application/stream+json";
	String LOGS_URI = "/logs";
	String LOGS_ENCOUNTERED_EXCPTIONS = "/logs/encountered/exceptions";
	String LOGS_ENCOUNTERED_ARTIFACTS = "/logs/encountered/artifacts";
	String LOGS_DISTRIBUTION_ARTIFACT = "/logs/distribution/artifact";
	

	// Errors & exceptions
	String ERROR_MESSAGE = "error";
	String AUTHENTICATION_ERROR = "authentication error";
	String AUTHORIZATION_ERROR = "authorization error";
	String BAD_REQUEST_ERROR = "bad request error";
	@NotEmpty
	String ARTIFACT_AUTHENTICATION = ARTIFACT + LogType.AUTHENTICATION_EXCEPTION;
	@NotEmpty
	String ARTIFACT_AUTHORIZATION = ARTIFACT + LogType.AUTHORIZATION_EXCEPTION;
	@NotEmpty
	String ARTIFACT_BAD_REQUEST = ARTIFACT + LogType.BAD_REQUEST_EXCEPTION;
	@NotEmpty
	String ARTIFACT_NO_EXEPTION = ARTIFACT + LogType.NO_EXCEPTION;
	
	
	
	
	
	
	
	
	
}