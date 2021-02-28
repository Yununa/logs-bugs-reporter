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
class BugsReporterController {
	static Logger LOG = LoggerFactory.getLogger(BugsReporterController.class);
	@Autowired
	BugsReporter bugsReporter;
	
	@PostMapping(BUGS_PROGRAMMERS)
	ProgrammerDto addProgrammer(@Valid @RequestBody ProgrammerDto programmerDto){
	         ProgrammerDto result = bugsReporter.addProgrammer(programmerDto);
		LOG.debug("\nProgrammer is added with id = {}\n", result.id);
		return result;
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

	@PutMapping(BUGS_ASSIGN)
	void assignBug(@Valid @RequestBody AssignBugData assignData){
	bugsReporter.assignBug(assignData);
	LOG.debug("\nAssigned bug data\n");
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

}
