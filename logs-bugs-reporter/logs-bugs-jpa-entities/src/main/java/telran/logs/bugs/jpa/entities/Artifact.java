package telran.logs.bugs.jpa.entities;
import javax.persistence.*;
import static telran.logs.bugs.api.DtoConstants.*;

@Entity
@Table(name = ARTIFACTS)
public class Artifact {
	@Id
	@Column(name = ARTIFACT_ID)
	String artifsctId;
	@ManyToOne
	@JoinColumn(name = PROGRAMMER_ID, nullable = false)
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
