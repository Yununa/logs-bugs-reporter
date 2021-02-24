package telran.logs.bugs.controllers;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import telran.logs.bugs.dto.AssignBugData;
import telran.logs.bugs.dto.BugAssignDto;
import telran.logs.bugs.dto.BugDto;
import telran.logs.bugs.dto.BugResponseDto;
import telran.logs.bugs.dto.ProgrammerDto;
import telran.logs.bugs.impl.BugsReporterImpl;
import static telran.logs.bugs.api.DtoConstants.*;
import java.util.*;

import javax.validation.constraints.Min;

@RestController
public class BugsReporterController {
	static Logger LOG = LoggerFactory.getLogger(BugsReporterController.class);
	@Autowired
	BugsReporterImpl bugsReporterImpl;
	
	@GetMapping(value = BUGS_PROGRAMMERS)
	public ProgrammerDto addProgrammer(@Value(value = PROGRAMMER) ProgrammerDto programmerDto){
	         ProgrammerDto result = bugsReporterImpl.addProgrammer(programmerDto);
		LOG.debug("Programmer is added");
		return result;
	}
	
	@GetMapping(value = BUGS_OPEN)
	BugResponseDto openBug(@Value(value = OPENING_METHOD) BugDto bugDto){
		LOG.debug("The bug is opened");
		return bugsReporterImpl.openBug(bugDto);
	}
	@GetMapping(value = BUGS_OPEN_ASSIGN)
	public BugResponseDto openAndAssingBug(@Value(value = ASSIGNER_NAME) BugAssignDto bugDto){
		LOG.debug("the bug is opened and assugned");
		return bugsReporterImpl.openAndAssingBug(bugDto);
	}
	
	@GetMapping(value = BUGS_ASSIGN)
	public void assignBug(@Value(value = ASSIGNER_NAME) AssignBugData assignData){
	bugsReporterImpl.assignBug(assignData);
		LOG.debug("Assign bug data");
	}
	@GetMapping(value = BUGS_PROGRAMMERS)
	public List<BugResponseDto> getBugsProgrammer(@PathVariable(name = ID) @Min(1) long programmerId){
		List<BugResponseDto> result = bugsReporterImpl.getBugsProgrammer(programmerId);
		LOG.debug("\nProgrammer Id is added to list with size: {}", result.size());
		return result;
	}

}
