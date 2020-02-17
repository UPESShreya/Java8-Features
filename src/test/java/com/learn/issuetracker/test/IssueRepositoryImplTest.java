package com.learn.issuetracker.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.meanbean.test.BeanTester;
import org.meanbean.test.Configuration;
import org.meanbean.test.ConfigurationBuilder;

import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;
import com.learn.issuetracker.repository.IssueRepositoryImpl;

public class IssueRepositoryImplTest {
	private static IssueRepositoryImpl dao;

	@BeforeClass
	public static void setUp() {
		dao = new IssueRepositoryImpl(Paths.get("src", "data", "issues.csv"));
	}

	@AfterClass
	public static void tearDown() {
		dao = null;
	}

	@Test
	public void testGetIssues() {
		List<Issue> issues = dao.getIssues();
		assertNotNull("List of issues cannot be null",issues);
	}

	@Test
	public void testInitializeIssuesFromFile() {
		assertNotNull("constructor should assign the list of issues read from the file to issues variable"
				+ " and should not be null", dao.getIssues());

	}

	@Test
	public void testGetOneIssue() {
		List<Issue> issues = dao.getIssues();
		assertNotNull("The issues variable doesn't seem to be initialized in the constructor", issues);
		assertFalse("The issues list should contain the data read from the file", issues.isEmpty());
		Issue issue = issues.get(0);
		assertNotNull("Issue Id is not initialized", issue.getIssueId());
		assertNotNull("createdOn is not initialized", issue.getCreatedOn());
		assertNotNull("status Id is not initialized", issue.getStatus());
	}

	@Test
	public void testIssueBean() {
		Configuration config = new ConfigurationBuilder().ignoreProperty("createdOn")
				.ignoreProperty("expectedResolutionOn").ignoreProperty("assignedTo").build();
		new BeanTester().testBean(Issue.class, config);
		Issue issue = new Issue();
		issue.setCreatedOn(LocalDate.now());
		assertNotNull("The created date should be set",issue.getCreatedOn());
		issue.setAssignedTo(new Employee());
		assertNotNull("The issue should be assigned to Employee",issue.getAssignedTo());
	}

}