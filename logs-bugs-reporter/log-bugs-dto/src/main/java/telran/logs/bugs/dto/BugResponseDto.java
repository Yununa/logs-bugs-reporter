package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BugResponseDto extends BugAssignDto {
	@Min(1)
	public long bugId;
	public LocalDate dateClose;
	public BugStatus bagstatus;
	public OpeningMethod openingMethod;

	public BugResponseDto(@NotNull Seriousness seriousness, @NotEmpty String description, LocalDate dateOpen,
			@Min(1) long programmerId, @Min(1) long bugId, LocalDate dateClose, BugStatus bagstatus,
			OpeningMethod openingMethod) {
		super(seriousness, description, dateOpen, programmerId);
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.bagstatus = bagstatus;
		this.openingMethod = openingMethod;
	}
	public BugResponseDto() {
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bagstatus == null) ? 0 : bagstatus.hashCode());
		result = prime * result + (int) (bugId ^ (bugId >>> 32));
		result = prime * result + ((dateClose == null) ? 0 : dateClose.hashCode());
		result = prime * result + ((openingMethod == null) ? 0 : openingMethod.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BugResponseDto other = (BugResponseDto) obj;
		if (bagstatus != other.bagstatus)
			return false;
		if (bugId != other.bugId)
			return false;
		if (dateClose == null) {
			if (other.dateClose != null)
				return false;
		} else if (!dateClose.equals(other.dateClose))
			return false;
		if (openingMethod != other.openingMethod)
			return false;
		return true;
	}


}
