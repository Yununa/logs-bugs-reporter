package telran.logs.bugs.interfaces;

import java.util.*;

import telran.logs.bugs.dto.*;

public interface BugsReporter {
	String ASSIGNMENT_DESCRIPTION_TITLE = "\nAssignment Description: ";
	String CLOSE_DATA_DESCRIPTION_TITLE ="\nClosing description:\n";
	ProgrammerDto addProgrammer(ProgrammerDto programmerDto);
	ArtifactDto addArtifact(ArtifactDto artifactDto);
	BugResponseDto openBug(BugDto bugDto);
	BugResponseDto openAndAssingBug(BugAssignDto bugDto);
	void assignBug(AssignBugData assignData);
	List<BugResponseDto> getNonAssignedBugs();
	void closeBug(CloseBugData closeData);
	List<BugResponseDto> getUnClosedBugsDuration(int days);
	List<BugResponseDto> getBugsProgrammer(long programmerId);
	List<EmailBugsCount> getEmailBugsCounts();
	List<String> getProgrammersMostBugs(int nProgrammers);
	List<String> getProgrammersLeastBugs(int nProgrammers);
	List<SeriousnessBugCount> getSeriousnessBugCounts();
	List<Seriousness> getSeriousnessTypesWithMostBugs(int nTypes);
	
}