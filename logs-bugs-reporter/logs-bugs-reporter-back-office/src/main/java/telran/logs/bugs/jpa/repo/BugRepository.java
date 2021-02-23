package telran.logs.bugs.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import telran.logs.bugs.jpa.entities.Bug;

@Service
public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);
	
}
