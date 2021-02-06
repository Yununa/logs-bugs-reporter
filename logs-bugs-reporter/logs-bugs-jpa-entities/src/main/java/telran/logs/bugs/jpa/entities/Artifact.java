package telran.logs.bugs.jpa.entities;
import javax.persistence.*;
@Entity
@Table(name = "artifacts")
public class Artifact {
	@Id
	@Column(name = "artifact_id")
	String artifsctId;
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	Programmer programmer;
	public Artifact() {
		
	}
	public Artifact(String artifsctId, Programmer programmer) {
		super();
		this.artifsctId = artifsctId;
		this.programmer = programmer;
	}
	public String getArtifsctId() {
		return artifsctId;
	}
	public Programmer getProgrammer() {
		return programmer;
	}
	

}
