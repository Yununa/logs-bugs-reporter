package telran.logs.bugs.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.*;
import telran.logs.bugs.jpa.repo.*;

@Service
public class BugsReporterImpl implements BugsReporter {

	BugRepository bugRepository;
	ArtifactRepository artifactRepository;
	ProgrammerRepository programmerRepository;
	@Autowired
	public BugsReporterImpl(BugRepository bugRepository, ArtifactRepository artifactRepository,
			ProgrammerRepository programmerRepository) {
		this.bugRepository = bugRepository;
		this.artifactRepository = artifactRepository;
		this.programmerRepository = programmerRepository;
	}

	@Override
	@Transactional
	public ProgrammerDto addProgrammer(ProgrammerDto programmerDto) {
		// FIXME exceptions had handling and key duplication check
		programmerRepository.save(new Programmer(programmerDto.id, programmerDto.name, programmerDto.email));
		return programmerDto;
	}

	@Override
	public ArtifactDto addArtifact(ArtifactDto artifactDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public BugResponseDto openBug(BugDto bugDto) {
		//FIXME exceptions
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.OPENED,
				bugDto.seriousness, OpeningMethod.MANUAL, null);
		bugRepository.save(bug);
		return toBugResponseDto(bug);
	}

	private BugResponseDto toBugResponseDto(Bug bug) {
		Programmer programmer = bug.getProgrammer();
		long programmerId = programmer == null ? 0 : programmer.getId();
		return new BugResponseDto(bug.getSeriousness(),bug.getDescription(), bug.getDateOpen(),
				 programmerId, bug.getId(),bug.getDateClose(), bug.getStatus(), bug.getOpeningMethod());
	}

	@Override
	@Transactional
	public BugResponseDto openAndAssingBug(BugAssignDto bugDto) {
		// FIXME exceptions
		Programmer programmer = programmerRepository.findById(bugDto.programmerId).orElse(null);
		//TODO exception in the case programmer is null
		LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
		Bug bug = new Bug(bugDto.description, dateOpen, null, BugStatus.ASSIGNED, bugDto.seriousness, OpeningMethod.MANUAL, programmer);
		bug =  bugRepository.save(bug);
		return toBugResponseDto(bug) ;
	}

	@Override
	@Transactional
	public void assignBug(AssignBugData assignData) {
		//FIXME exceptions
		Bug bug = bugRepository.findById(assignData.bugId).orElse(null);
		bug.setDescription(bug.getDescription() + BugsReporter.ASSIGNMENT_DESCRIPTION_TITLE + assignData.description);
		Programmer programmer = programmerRepository.findById(assignData.programmerId).orElse(null);
		bug.setStatus(BugStatus.ASSIGNED);
		bug.setProgrammer(programmer);
	}
	
	@Override
	public List<BugResponseDto> getNonAssignedBugs() {		
		List<Bug> bugs = bugRepository.findByStatus(BugStatus.OPENED);
		return toListBugResponseDto(bugs);
	}

	
	@Override
	public void closeBug(CloseBugData closeData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BugResponseDto> getUnClosedBugsDuration(int days) {
		LocalDate dateOpen = LocalDate.now().minusDays(days);
		List<Bug> bugs = bugRepository.findByStatusNotAndDateOpenBefore(BugStatus.CLOSED, dateOpen);
		return toListBugResponseDto(bugs);
	}

	@Override
	public List<BugResponseDto> getBugsProgrammer(long programmerId) {
		List<Bug> bugs = bugRepository.findByProgrammerId(programmerId);
		return bugs.isEmpty() ? new LinkedList<>() : toListBugResponseDto(bugs);
	}

	private List<BugResponseDto> toListBugResponseDto(List<Bug> bugs) {
		
		return bugs.stream().map(this::toBugResponseDto).collect(Collectors.toList());
	}

	@Override
	public List<EmailBugsCount> getEmailBugsCounts() {
		List<EmailBugsCount> result = bugRepository.emailBugsCounts();
		return result;
	}

	@Override
	public List<String> getProgrammersMostBugs(int nProgrammer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getProgrammersLeastBugs(int nProgrammers) {
		// TODO Auto-generated method stub
		return null;
	}

}