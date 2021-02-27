package telran.logs.bugs.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BugResponseDto extends BugAssignDto {
	
	public long bugId;
	public LocalDate dateClose;
	public BugStatus bagStatus;
	public OpeningMethod openingMethod;

	public BugResponseDto(@NotNull Seriousness seriousness, @NotEmpty String description, LocalDate dateOpen,
			@Min(1) long programmerId, long bugId, LocalDate dateClose, BugStatus bagStatus,
			OpeningMethod openingMethod) {
		super(seriousness, description, dateOpen, programmerId);
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.bagStatus = bagStatus;
		this.openingMethod = openingMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(bagStatus, bugId, dateClose, openingMethod);
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
		return bagStatus == other.bagStatus && bugId == other.bugId && Objects.equals(dateClose, other.dateClose)
				&& openingMethod == other.openingMethod;
	}
	




}
