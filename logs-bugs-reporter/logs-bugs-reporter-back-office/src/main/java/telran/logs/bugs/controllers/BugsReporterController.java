package telran.logs.bugs.controllers;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import static telran.logs.bugs.api.DtoConstants.*;
import java.util.*;
import javax.validation.Valid;


@RestController
public class BugsReporterController {
	static Logger LOG = LoggerFactory.getLogger(BugsReporterController.class);
	@Autowired
	BugsReporter bugsReporter;
	
	@PostMapping(BUGS_PROGRAMMERS)
	ProgrammerDto addProgrammer(@Valid @RequestBody ProgrammerDto programmerDto){
	         ProgrammerDto result = bugsReporter.addProgrammer(programmerDto);
		LOG.debug("\nProgrammer is added with id = {}\n", result.id);
		return result;
	}
	
	@PostMapping(BUGS_ARTIFACTS)
	ArtifactDto addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
		ArtifactDto result = bugsReporter.addArtifact(artifactDto);
		LOG.debug("\nArtifact with Id: {} is added", result.artifactId);
		return result ;
		
	}

	
	@PostMapping(BUGS_OPEN)
	BugResponseDto openBug(@Valid @RequestBody BugDto bugDto){
		BugResponseDto result = bugsReporter.openBug(bugDto);
		LOG.debug("\nOpened bug is saved with id = {},\nDescription: {},\n Status: {}\n",result.bugId, result.description, result.bagStatus);
		return result;
	} 
	@PostMapping(BUGS_OPEN_ASSIGN)
	BugResponseDto openAndAssingBug(@Valid @RequestBody BugAssignDto bugDto){
		BugResponseDto result = bugsReporter.openAndAssingBug(bugDto);
		LOG.debug("\nOpened & assigned bug is saved with id = {},\nDescription: {},\n Status: {}\n",result.bugId, result.description,result.bagStatus);
		return result;
	}
	
	@PutMapping(BUGS_CLOSE)
	void closeBug(@Valid @RequestBody CloseBugData closeData) {
		bugsReporter.closeBug(closeData);
		LOG.debug("\nBug with id: {} closed\n", closeData.bugId);
	}
	
	@PutMapping(BUGS_ASSIGN)
	void assignBug(@Valid @RequestBody AssignBugData assignData){
	bugsReporter.assignBug(assignData);
	LOG.debug("\nAssigned bug data\n");
	}
	
	@GetMapping(BUGS_ASSIGNED_NOT)
	List<BugResponseDto> getNonAssignedBugs(){
		List<BugResponseDto> result = bugsReporter.getNonAssignedBugs();
		LOG.debug("\ncount: {} of not assigned bugs\n", result.size());
		return result;
	}
	
	@GetMapping(BUGS_PROGRAMMERS)
	List<BugResponseDto> getBugsProgrammer(@RequestParam(name=PROGRAMMER_ID) long programmerId){
		List<BugResponseDto> result = bugsReporter.getBugsProgrammer(programmerId);
		LOG.debug("\nTo programmer with id = {},\nfound {} bugs\n",programmerId, result.size());
		return result;
	}
	
	@GetMapping(BUGS_PROGRAMMERS_COUNT)
	List<EmailBugsCount> getEmailBugsCount() {
		List<EmailBugsCount> result = bugsReporter.getEmailBugsCounts();
		result.forEach(ec -> LOG.debug("\nemail: {}\ncount: {}\n",ec.getEmail(),ec.getCount()));
		return result ;
	}
	
	@GetMapping(BUGS_UNCLOSED) 
	List<BugResponseDto> getUnClosedBugsDuration(@RequestParam(N_DAYS)int days){
		List<BugResponseDto> result = bugsReporter.getUnClosedBugsDuration(days);
	    LOG.debug("\n{} bugs not cloused in : {} after date open", result.size(), days);
		return result ;
	}
	
	@GetMapping(BUGS_PROGRAMMERS_MOST)
	List<String> getMostBugsProgrammers(@RequestParam(name=N_PROGRAMMERS, defaultValue = "2") int nProgrammers) {
		List<String> res = bugsReporter.getProgrammersMostBugs(nProgrammers);
		LOG.debug("getMostBugsProgrammers: list of programmers {}", res);
		return res ;
	}
	
	@GetMapping(BUGS_PROGRAMMERS_LEAST)
	List<String> getLeastBugsProgrammers(@RequestParam(name = N_PROGRAMMERS, defaultValue = "2") int nProgrammers) {
		List<String> res = bugsReporter.getProgrammersLeastBugs(nProgrammers);
		LOG.debug("getMostBugsProgrammers: list of programmers {}", res);
		return res ;
	}
	
	@GetMapping(BUGS_SERIOUSNESS_COUNT)
	List<SeriousnessBugCount> getSeriousnessBugsCounts() {
		List<SeriousnessBugCount> res = bugsReporter.getSeriousnessBugCounts();
		res.forEach(bc -> LOG.debug("seriousness: {}; count: {}", bc.getSeriousness(), bc.getCount())); 
		return res ;
	}
	
	@GetMapping(BUGS_SERIOUSNESS_MOST)
	List<Seriousness> getSeriousnessBugsMost(@RequestParam (name = N_TYPES, defaultValue = "2") int nTypes) {
		List<Seriousness> res = bugsReporter.getSeriousnessTypesWithMostBugs(nTypes);
		LOG.debug("List of seriousness types with most bugs {}", res);
		return res;
	}
}
