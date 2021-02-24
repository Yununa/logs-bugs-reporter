package telran.logs.bugs.controllers;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.interfaces.BugsReporter;
import static telran.logs.bugs.api.DtoConstants.*;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
class BugsReporterController {
	static Logger LOG = LoggerFactory.getLogger(BugsReporterController.class);
	@Autowired
	BugsReporter bugsReporter;
	
	@GetMapping(BUGS_PROGRAMMERS)
	ProgrammerDto addProgrammer(@Valid @RequestBody ProgrammerDto programmerDto){
	         ProgrammerDto result = bugsReporter.addProgrammer(programmerDto);
		LOG.debug("Programmer is added");
		return result;
	}
	
	@GetMapping(BUGS_OPEN)
	BugResponseDto openBug(@Valid @RequestBody BugDto bugDto){
		LOG.debug("The bug is opened");
		return bugsReporter.openBug(bugDto);
	}
	@GetMapping(BUGS_OPEN_ASSIGN)
	BugResponseDto openAndAssingBug(@Valid @RequestBody BugAssignDto bugDto){
		LOG.debug("the bug is opened and assugned");
		return bugsReporter.openAndAssingBug(bugDto);
	}
	
	@GetMapping(BUGS_ASSIGN)
	void assignBug(@Valid @RequestBody AssignBugData assignData){
	bugsReporter.assignBug(assignData);
		LOG.debug("Assign bug data");
	}
	@GetMapping(BUGS_PROGRAMMERS)
	List<BugResponseDto> getBugsProgrammer(@RequestParam(name = PROGRAMMER_ID) @Min(1) long programmerId){
		List<BugResponseDto> result = bugsReporter.getBugsProgrammer(programmerId);
		LOG.debug("\nProgrammer Id is added to list with size: {}", result.size());
		return result;
	}

}
