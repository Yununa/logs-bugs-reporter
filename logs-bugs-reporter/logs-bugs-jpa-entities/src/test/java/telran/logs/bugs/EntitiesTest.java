package telran.logs.bugs;

import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.*;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = { ProgrammersRepo.class, ArtifactsRepo.class, BugsRepo.class })
public class EntitiesTest {
	@Autowired
	ProgrammersRepo programmers;
	@Autowired
	ArtifactsRepo artifacts;
	@Autowired
	BugsRepo bugs;

	@Test
	void bugCreation() {
		Programmer programmer = new Programmer(123, "Moshe", "moshe@gmail.com");
		programmers.save(programmer);
		Artifact artifact = new Artifact("authentication", programmer);
		artifacts.save(artifact);
		Bug bug = new Bug("description", LocalDate.now(), null, BugStatus.ASSIGNED, Seriousness.MINOR,
				OpeningMethod.AUTHOMATIC, programmer);
		bugs.save(bug);
		List<Bug> bugsList = bugs.findAll();
		assertEquals(1, bugsList.size());
		assertEquals(bug, bugsList.get(0));
	}

}