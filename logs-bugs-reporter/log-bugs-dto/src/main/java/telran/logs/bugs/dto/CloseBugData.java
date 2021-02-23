package telran.logs.bugs.dto;

import java.time.LocalDate;
import javax.validation.constraints.*;

public class CloseBugData {
	@Min(1)
	public long bugId;
	public LocalDate dateClose;
	public String description;
	public CloseBugData(@Min(1) long bugId, LocalDate dateClose, String description) {
		super();
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.description = description;
	}

}