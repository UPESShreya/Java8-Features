package com.learn.issuetracker.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//import java.util.stream.Collectors;

import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;

/*
 * This class has methods for parsing the String read from the files in to corresponding Model Objects
*/
public class Utility {
	
	private Utility() {
		//Private Constructor to prevent object creation
		super();
	}

	/*
	 * parseEmployee takes a string with employee details as input parameter and parses it in to an Employee Object 
	*/
	public static Employee parseEmployee(String employeeDetail) {
		//Employee e=Employee.stream().collect(Collectors.mapping(p->p.getEmployee(), Collectors.toList()));
		Employee employee=null;
		String[] rows=employeeDetail.split(",");
		if(rows.length!=0)
		{
			employee=new Employee(Integer.parseInt(rows[0]),rows[1],rows[2]);
		}
		//return e;
		return employee;
	}

	/*
	 * parseIssue takes a string with issue details and parses it in to an Issue Object. The employee id in the 
	 * Issue details is used to search for an an Employee, using EmployeeRepository class. If the employee is found
	 * then it is set in the Issue object. If Employee is not found, employee is set as null in Issue Object  
	*/

	public static Issue parseIssue(String issueDetail) {
		Issue issue = null;
		String[] row=issueDetail.split(",");
		if(row.length !=0)
		{
			DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
			issue=new Issue(row[0],row[1],LocalDate.parse(row[2],dateFormatter),LocalDate.parse(row[3],dateFormatter),row[4],row[5],EmployeeRepository.getEmployee(Integer.parseInt(row[6])).orElse(null));
		}
		return issue;
	}
}
