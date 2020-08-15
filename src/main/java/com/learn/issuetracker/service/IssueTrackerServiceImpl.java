package com.learn.issuetracker.service;

//import java.time.Duration;
import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
//import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
//import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.learn.issuetracker.exceptions.IssueNotFoundException;
import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;
import com.learn.issuetracker.repository.EmployeeRepository;
import com.learn.issuetracker.repository.IssueRepository;

/*
 * This class contains functionalities for searching and analyzing Issues data Which is stored in a collection
 * Use JAVA8 STREAMS API to do the analysis
 * 
*/
public class IssueTrackerServiceImpl implements IssueTrackerService {
	
	//static DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
	/*
	 * CURRENT_DATE contains the date which is considered as todays date for this
	 * application Any logic which uses current date in this application, should
	 * consider this date as current date
	 */
	private static final String CURRENT_DATE = "2019-05-01";
	private static final String CLOSED_STATUS="CLOSED";
	private static final String OPEN_STATUS ="OPEN";
	private static final String HIGH_PRIORITY="HIGH";
	private static final String MEDIUM_PRIORITY="MEDIUM";

	/*
	 * The issueDao should be used to get the List of Issues, populated from the
	 * file
	 */
	private IssueRepository issueDao;
	private LocalDate today;

	/*
	 * Initialize the member variables Variable today should be initialized with the
	 * value in CURRENT_DATE variable
	 */
	public IssueTrackerServiceImpl(IssueRepository issueDao) {
		this.issueDao=issueDao;
		this.today=LocalDate.parse(CURRENT_DATE);
	}

	/*
	 * In all the below methods, the list of issues should be obtained by used
	 * appropriate getter method of issueDao.
	 */
	/*
	 * The below method should return the count of issues which are closed.
	 */
	@Override
	public long getClosedIssueCount() {
		long count=0;
		List<Issue> issueList=this.issueDao.getIssues();
		if(issueList.size()!=0)
		{
			count=issueList.stream().filter(i->i.getStatus().equals(CLOSED_STATUS)).count();
		}
		return count;
	}

	/*
	 * The below method should return the Issue details given a issueId. If the
	 * issue is not found, method should throw IssueNotFoundException
	 */

	@Override
	public Issue getIssueById(String issueId) throws IssueNotFoundException {
		List<Issue> issueDetails=this.issueDao.getIssues();
		Issue issue=null;
		try {
		if(issueDetails.size()!=0)
		{
			issue=issueDetails.stream().filter(i->i.getIssueId().equals(issueId)).findAny().orElse(null);
		}
		}catch(IssueNotFoundException e) {
			e.printStackTrace();
		}
		return issue;
		
		
		//return issue;
	}

	/*
	 * The below method should return the Employee Assigned to the issue given a
	 * issueId. It should return the employee in an Optional. If the issue is not
	 * assigned to any employee or the issue Id is incorrect the method should
	 * return empty optional
	 */
	@Override
	public Optional<Employee> getIssueAssignedTo(String issueId) {
List<Issue> issuelist = issueDao.getIssues();
		
		return issuelist.stream()
						.filter(issue -> issue.getIssueId() == issueId)
						.map(employee -> employee.getAssignedTo())
						.findAny();
	}

	/*
	 * The below method should return the list of Issues given the status. The
	 * status can contain values OPEN / CLOSED
	 */
	@Override
	public List<Issue> getIssuesByStatus(String status) {
		
		return issueDao.getIssues().stream().filter(i->i.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
		
		
	}

	/*
	 * The below method should return a LinkedHashSet containing issueid's of open
	 * issues in the ascending order of expected resolution date
	 */
	@Override
	public Set<String> getOpenIssuesInExpectedResolutionOrder() {
		LinkedHashSet<String> n=null;
		List<Issue> issueDetails=issueDao.getIssues();
		n=issueDetails.stream().filter(s->s.getStatus().equalsIgnoreCase(OPEN_STATUS)).sorted(Comparator.comparing(Issue::getExpectedResolutionOn)).map(Issue::getIssueId).collect(Collectors.toCollection( LinkedHashSet::new ));
		return n;
	}

	/*
	 * The below method should return a List of open Issues in the descending order
	 * of Priority and ascending order of expected resolution date within a priority
	 */
	@Override
	public List<Issue> getOpenIssuesOrderedByPriorityAndResolutionDate() {
		List<Issue> issueByPriority=null;
		issueByPriority=issueDao.getIssues().stream().filter(s->s.getStatus().equalsIgnoreCase(OPEN_STATUS)).
					sorted(Comparator.comparing(Issue::getPriority).reversed().
							thenComparing(Comparator.comparing(Issue::getExpectedResolutionOn))).collect(Collectors.toList());
		return issueByPriority;
	}

	/*
	 * The below method should return a List of 'unique' employee names who have
	 * issues not closed even after 7 days of Expected Resolution date. Consider the
	 * current date as 2019-05-01
	 */
	@Override
	public List<String> getOpenIssuesDelayedbyEmployees() {
		List<Issue> issueList = issueDao.getIssues();

		return issueList.stream()
						.filter(issue -> issue.getStatus() == "OPEN")
						.filter(issue -> issue.getExpectedResolutionOn().plusDays(7).isBefore(LocalDate.parse(CURRENT_DATE)))
						.distinct()
						.map(issue->issue.getAssignedTo().getName())
						.collect(Collectors.toList());
	}

	/*
	 * The below method should return a map with key as issueId and value as
	 * assigned employee Id. THe Map should contain details of open issues having
	 * HIGH priority
	 */
	@Override
	public Map<String, Integer> getHighPriorityOpenIssueAssignedTo() {
		
		Map<String, Integer> highPriority=issueDao.getIssues().stream().filter(p->p.getPriority().equals(HIGH_PRIORITY)).filter(s->s.getStatus().equals(OPEN_STATUS)).collect(Collectors.toMap(k->k.getIssueId(), i->i.getAssignedTo().getEmplId()));
		return highPriority;
	}

	/*
	 * The below method should return open issues grouped by priority in a map. The
	 * map should have key as issue priority and value as list of open Issues
	 */
	@Override
	public Map<String, List<Issue>> getOpenIssuesGroupedbyPriority() {
		
		Map<String, List<Issue>> groupedByPriority=issueDao.getIssues().stream().filter(s->s.getStatus().equals(OPEN_STATUS)).collect(Collectors.groupingBy(Issue::getPriority));
		return groupedByPriority;
	}

	/*
	 * The below method should return count of open issues grouped by priority in a map. 
	 * The map should have key as issue priority and value as count of open issues 
	 */
	@Override
	public Map<String, Long> getOpenIssuesCountGroupedbyPriority() {
		Map<String, Long> countByPriority=issueDao.getIssues().stream().filter(s->s.getStatus().equals(OPEN_STATUS)).collect(Collectors.groupingBy(Issue::getPriority,Collectors.mapping(Issue::getIssueId, Collectors.counting())));
		return countByPriority;
	}
	
	/*
	 * The below method should provide List of issue id's(open), grouped by location
	 * of the assigned employee. It should return a map with key as location and
	 * value as List of issue Id's of open issues
	 */
	@Override
	public Map<String, List<String>> getOpenIssueIdGroupedbyLocation() {
		Map<String, List<String>> groupByLocation=issueDao.getIssues().stream().filter(s->s.getStatus().equals(OPEN_STATUS)).collect(Collectors.groupingBy(l->l.getAssignedTo().getLocation(), Collectors.mapping(Issue::getIssueId, Collectors.toList())));
		return groupByLocation;
	}
	
	/*
	 * The below method should provide the number of days, since the issue has been
	 * created, for all high/medium priority open issues. It should return a map
	 * with issueId as key and number of days as value. Consider the current date as
	 * 2019-05-01
	 */
	@Override
	public Map<String, Long> getHighMediumOpenIssueDuration() {
		today=LocalDate.of(2019,05,01);
		Map<String, Long> duration=issueDao.getIssues().stream().filter(s->s.getStatus().equals(OPEN_STATUS)).filter(s->(s.getPriority().equals(HIGH_PRIORITY) || s.getPriority().equals(MEDIUM_PRIORITY))).collect(Collectors.toMap(Issue::getIssueId,issue-> ChronoUnit.DAYS.between(issue.getCreatedOn(),today)));
		return duration;
	}
}