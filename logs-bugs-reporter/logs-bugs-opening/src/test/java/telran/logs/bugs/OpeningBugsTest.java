package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import telran.logs.bugs.repository.ProgrammersRepo;
import telran.logs.bugs.jpa.entities.Programmer;

@SpringBootTest
@AutoConfigureTestDatabase
public class OpeningBugsTest {
	@Autowired
	ProgrammersRepo progRepo;
	@Test
	void primitivTest() {
		progRepo.save(new Programmer(123, "Moshe"));
	}

}
