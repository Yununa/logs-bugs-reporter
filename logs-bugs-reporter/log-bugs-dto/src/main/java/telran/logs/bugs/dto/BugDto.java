package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.*;

public class BugDto {
	@NotNull
	public Seriousness seriousness;
	@NotEmpty
	public String description;
	public LocalDate dateOpen;
	public BugDto(@NotNull Seriousness seriousness, @NotEmpty String description, LocalDate dateOpen) {
		super();
		this.seriousness = seriousness;
		this.description = description;
		this.dateOpen = dateOpen;
	}
	public BugDto() {
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateOpen == null) ? 0 : dateOpen.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((seriousness == null) ? 0 : seriousness.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BugDto other = (BugDto) obj;
		if (dateOpen == null) {
			if (other.dateOpen != null)
				return false;
		} else if (!dateOpen.equals(other.dateOpen))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (seriousness != other.seriousness)
			return false;
		return true;
	}
}