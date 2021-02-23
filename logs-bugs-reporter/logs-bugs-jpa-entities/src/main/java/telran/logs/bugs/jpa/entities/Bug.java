package telran.logs.bugs.jpa.entities;
import java.time.LocalDate;

import javax.persistence.*;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpeningMethod;
import telran.logs.bugs.dto.Seriousness;
@Entity
@Table(name = "bugs")
public class Bug {
	@Id
	@GeneratedValue
	long id;  
	@Column(nullable = false)
	String description;
	@Column(name = "date_open", nullable = false)
	LocalDate dateOpen;
	@Column(name = "date_close", nullable = true)
	LocalDate dateClose;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	BugStatus status;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Seriousness seriousness;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "openning_method")
	OpeningMethod openningMethod;
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = true)
	Programmer programmer;
	public Bug() {
		
	}
	public Bug(String description, LocalDate dateOpen, LocalDate dateClose, BugStatus status, Seriousness seriousness,
			OpeningMethod openningMethod, Programmer programmer) {
		super();
		this.description = description;
		this.dateOpen = dateOpen;
		this.dateClose = dateClose;
		this.status = status;
		this.seriousness = seriousness;
		this.openningMethod = openningMethod;
		this.programmer = programmer;
	}
	
	public long getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	public LocalDate getDateOpen() {
		return dateOpen;
	}
	public LocalDate getDateClose() {
		return dateClose;
	}
	public BugStatus getStatus() {
		return status;
	}
	public Seriousness getSeriousness() {
		return seriousness;
	}
	public OpeningMethod getOpenningMethod() {
		return openningMethod;
	}
	public Programmer getProgrammer() {
		return programmer;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDateClose(LocalDate dateClose) {
		this.dateClose = dateClose;
	}
	public void setStatus(BugStatus status) {
		this.status = status;
	}
	public void setSeriousness(Seriousness seriousness) {
		this.seriousness = seriousness;
	}
	public void setProgrammer(Programmer programmer) {
		this.programmer = programmer;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateClose == null) ? 0 : dateClose.hashCode());
		result = prime * result + ((dateOpen == null) ? 0 : dateOpen.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((openningMethod == null) ? 0 : openningMethod.hashCode());
		result = prime * result + ((programmer == null) ? 0 : programmer.hashCode());
		result = prime * result + ((seriousness == null) ? 0 : seriousness.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Bug other = (Bug) obj;
		if (dateClose == null) {
			if (other.dateClose != null)
				return false;
		} else if (!dateClose.equals(other.dateClose))
			return false;
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
		if (openningMethod != other.openningMethod)
			return false;
		if (programmer == null) {
			if (other.programmer != null)
				return false;
		} else if (!programmer.equals(other.programmer))
			return false;
		if (seriousness != other.seriousness)
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	

}
