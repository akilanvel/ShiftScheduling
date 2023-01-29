// -----------------------------------------------------
// EMPLOYEE SCHEDULING APPLICATION
// -----------------------------------------------------
// Author: Akilan Gnanavel
// Made for University Emergency Medical Response
// -----------------------------------------------------

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

// using test data, it gives Crystal P (an EMT-2) 4 shifts instead of 1

public class ShiftScheduler {
	private Employee[] employees; // Holds list of employees (sorted by increasing availability)
	private Employee[][] shiftAssignments; // Holds all shift assignments (35 x 2)
	private int[] listOfShifts; // Holds shift indexes from 0 to 34 (35 total)
	private HashMap<Integer, String> shiftTitles; // Holds readable String corresponding to each of the 35 shift
													// integers 0-34

	// Constructor
	public ShiftScheduler(Employee[] e) {
		// Initialize instance variables
		employees = e;
		shiftAssignments = new Employee[35][2];
		listOfShifts = new int[35];
		shiftTitles = new HashMap<>();

		// Populate the listOfShifts array with corresponding values
		for (int i = 0; i < listOfShifts.length; i++) {
			listOfShifts[i] = i;
		}

		// Populate the shiftAssignments 2D array with null employees at each shift for
		// now
		for (int i = 0; i < shiftAssignments.length; i++) {
			for (int x = 0; x < shiftAssignments[i].length; x++) {
				shiftAssignments[i][x] = null;
			}
		}

		// Populate the shiftTitles HashMap with String corresponding to each shift (to
		// use for printing)
		shiftTitles.put(0, "Monday 8:20 AM to 11:20 AM");
		shiftTitles.put(1, "Monday 11:20 AM to 2:20 PM");
		shiftTitles.put(2, "Monday 2:20 PM to 5:20 PM");
		shiftTitles.put(3, "Monday 5:20 PM to 8:20 PM");
		shiftTitles.put(4, "Monday 8:20 PM to 11:20 PM");
		shiftTitles.put(5, "Tuesday 8:20 AM to 11:20 AM");
		shiftTitles.put(6, "Tuesday 11:20 AM to 2:20 PM");
		shiftTitles.put(7, "Tuesday 2:20 PM to 5:20 PM");
		shiftTitles.put(8, "Tuesday 5:20 PM to 8:20 PM");
		shiftTitles.put(9, "Tuesday 8:20 PM to 11:20 PM");
		shiftTitles.put(10, "Wednesday 8:20 AM to 11:20 AM");
		shiftTitles.put(11, "Wednesday 11:20 AM to 2:20 PM");
		shiftTitles.put(12, "Wednesday 2:20 PM to 5:20 PM");
		shiftTitles.put(13, "Wednesday 5:20 PM to 8:20 PM");
		shiftTitles.put(14, "Wednesday 8:20 PM to 11:20 PM");
		shiftTitles.put(15, "Thursday 8:20 AM to 11:20 AM");
		shiftTitles.put(16, "Thursday 11:20 AM to 2:20 PM");
		shiftTitles.put(17, "Thursday 2:20 PM to 5:20 PM");
		shiftTitles.put(18, "Thursday 5:20 PM to 8:20 PM");
		shiftTitles.put(19, "Thursday 8:20 PM to 11:20 PM");
		shiftTitles.put(20, "Friday 8:20 AM to 11:20 AM");
		shiftTitles.put(21, "Friday 11:20 AM to 2:20 PM");
		shiftTitles.put(22, "Friday 2:20 PM to 5:20 PM");
		shiftTitles.put(23, "Friday 5:20 PM to 8:20 PM");
		shiftTitles.put(24, "Friday 8:20 PM to 11:20 PM");
		shiftTitles.put(25, "Saturday 8:20 AM to 11:20 AM");
		shiftTitles.put(26, "Saturday 11:20 AM to 2:20 PM");
		shiftTitles.put(27, "Saturday 2:20 PM to 5:20 PM");
		shiftTitles.put(28, "Saturday 5:20 PM to 8:20 PM");
		shiftTitles.put(29, "Saturday 8:20 PM to 11:20 PM");
		shiftTitles.put(30, "Sunday 8:20 AM to 11:20 AM");
		shiftTitles.put(31, "Sunday 11:20 AM to 2:20 PM");
		shiftTitles.put(32, "Sunday 2:20 PM to 5:20 PM");
		shiftTitles.put(33, "Sunday 5:20 PM to 8:20 PM");
		shiftTitles.put(34, "Sunday 8:20 PM to 11:20 PM");
	}

	// This method schedules each employee
	public void scheduleShifts() {
		// Loop through all employees
		for (Employee employee : employees) {
			// Assign one or two shifts the the employee, based on rank
			if (employee.getType().equals("EMT-2")) {
				assignShift(employee, 1);
			} else if (employee.getType().equals("Pair")) {
				assignShift(employee, 2);
			}
		}
	}

	// This method assigns a specific shift to each employee
	private void assignShift(Employee employee, int numShifts) {
		for (int i = 0; i < numShifts; i++) {
			// find the next available shift
			int availableShift = findAvailableShift(employee);
			if (availableShift != -1) { // if there is an available shift, add employee to shift
				addToShift(availableShift, employee);
			} else { // if there isn't an available shift for the current employee that we're trying
						// to assign:
				// if we reach here, current shift assignments prevent the current employee from
				// being assigned a shift
				// loop through every employee in each of the current employee's availabilities
				// and try to reassign them
				boolean notPossible = true;

				for (int s : employee.getAvailability()) {
					Employee[] employees = shiftAssignments[s];
					for (Employee e : employees) {
						if (hasAvailableShift(e)) {
							for (int shift : e.getAvailability()) {
								if (e.isAvailable(shift)) {
									// e is old employee
									// employee is new employee
									// s is old shift
									// shift is new shift

									// Swap shifts:
									removeFromShift(s, e);
									addToShift(shift, e);
									addToShift(s, employee);

									notPossible = false;
								}
							}
						}
					}
				}
				if (notPossible) { // if shift swapping based on availabilities wasn't possible:
					System.out.println("Unable to assign shift for " + employee.getName());
				}
			}
		}
	}

	// This method specifies whether an employee has an open shift in their
	// availability that they aren't already a part of
	private boolean hasAvailableShift(Employee employee) {
		for (int s : employee.getAvailability()) {
			if (employee.isAvailable(s) && !isFull(s)) {
				return true;
			}
		}
		return false;
	}

	// This method specifies whether a shift is at max capacity (2 employees)
	private boolean isFull(int shift) {
		int numEmployees = 0;
		for (int i = 0; i < shiftAssignments[shift].length; i++) {
			if (shiftAssignments[shift][i] != null) {
				numEmployees++;
			}
		}
		return numEmployees == 2;
	}

	// This method removes an employee from a shift
	private void removeFromShift(int shift, Employee employee) {
		for (int i = 0; i < shiftAssignments[shift].length; i++) {
			// this comparison won't work if there are two employees with the same full name
			if (shiftAssignments[shift][i].getName().equals(employee.getName())) {
				// remove employee from the shift in the shiftAssignments 2D array
				shiftAssignments[shift][i] = null;
				// mark the employee is available for the shift (since they were just removed)
				employee.makeAvailable(shift);
			}
		}
	}

	// This method adds an employee to a shift
	private void addToShift(int shift, Employee employee) {
		for (int i = 0; i < shiftAssignments[shift].length; i++) {
			if (shiftAssignments[shift][i] == null) {
				// add employee to the shift in the shiftAssignments 2D array
				shiftAssignments[shift][i] = employee;
				// mark the employee as unavailable for the shift (since they were just added)
				employee.makeUnavailable(shift);
				break;
			}
		}
	}

	// This method finds the next available shift (that has room) in the employee's
	// availability
	// It prioritizes finding empty shifts first (to spread out shifts)
	private int findAvailableShift(Employee employee) {
		// first see if there are empty shifts
		for (int shift : listOfShifts) {
			if (employee.getAvailability().contains(shift) && employee.isAvailable(shift)
					&& numEmployeesOnShift(shift) == 0) {
				return shift;
			}
		}
		// only if there are no empty shifts, check in shifts with one person already
		// working
		for (int shift : listOfShifts) {
			if (employee.getAvailability().contains(shift) && employee.isAvailable(shift)
					&& numEmployeesOnShift(shift) < 2) {
				return shift;
			}
		}
		// Return -1 if no shift is available
		return -1;
	}

	// This method returns the number of employees working a specified shift
	private int numEmployeesOnShift(int shift) {
		int numEmployees = 0;
		for (int i = 0; i < shiftAssignments[shift].length; i++) {
			if (shiftAssignments[shift][i] != null) {
				numEmployees++;
			}
		}
		return numEmployees;
	}

	// This method prints the schedule in a readable format
	public void printSchedule() {
		for (int shift : listOfShifts) {
			System.out.print(shiftTitles.get(shift) + ":\t");
			for (Employee employee : shiftAssignments[shift]) {
				if (employee != null) {
					System.out.print(employee.getName() + ", ");
				}
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		// Read from the input file and create a list of employees
		ArrayList<String> input = readLines("file.txt"); // <<<- NAME OF FILE GOES IN THE QUOTATION MARKS
		Employee[] employees = new Employee[input.size()];
		for (int i = 0; i < input.size(); i++) {
			employees[i] = new Employee(input.get(i));
		}

		// sort the employees by increasing availability (prioritize less available
		// people)
		employees = sortByAvailability(employees);

		// Create ShiftScheduler object
		ShiftScheduler shiftScheduler = new ShiftScheduler(employees);
		shiftScheduler.scheduleShifts(); // assign all the shifts
		shiftScheduler.printSchedule(); // print the schedule
	}

	// This method reads the input from a specified file and returns each line in an
	// ArrayList of Strings
	public static ArrayList<String> readLines(String fileName) {
		ArrayList<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	// This method sorts an array of Employees and returns an array of Employees
	// sorted by increasing availability
	public static Employee[] sortByAvailability(Employee[] employees) {
		Arrays.sort(employees, (e1, e2) -> e1.getAvailability().size() - e2.getAvailability().size());
		return employees;
	}
}

class Employee {
	private String type; // rank
	private String name; // full name
	private ArrayList<Integer> availability; // available shifts
	private ArrayList<Boolean> active; // FALSE MEANS EMPLOYEE IS OPEN TO WORK AT THAT SHIFT, TRUE MEANS EMPLOYEE IS
										// ALREADY WORKING AT THAT SHIFT

	public Employee(String input) {
		// Assign the employee's instance variables based on the input line format
		// FORMAT IS AS FOLLOWS: "Qaim Akilan Pair EMT-2 0 7 12 34"
		String[] parameters = input.split(" ");
		type = parameters[2];
		name = parameters[0] + " " + parameters[1];
		availability = new ArrayList<>();
		active = new ArrayList<>();

		// assign their availability ot the employee
		for (int i = 3; i < parameters.length; i++) {
			availability.add(Integer.parseInt(parameters[i]));
			active.add(false); // the employee isn't working any shift yet, so every availability is false
		}
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Integer> getAvailability() {
		return availability;
	}

	// This method is to be used when assigning the employee to a shift
	public void makeUnavailable(int shift) {
		active.set(availability.indexOf(shift), true);
	}

	// This method is to be used when removing the employee from the shift
	public void makeAvailable(int shift) {
		active.set(availability.indexOf(shift), false);
	}

	public boolean isAvailable(int shift) {
		return !active.get(availability.indexOf(shift));
	}

	public ArrayList<Boolean> getActive() {
		return active;
	}

	// for bug testing
	public void printAvailability() {
		for (int i = 0; i < availability.size(); i++) {
			System.out.print(availability.get(i) + " " + active.get(i) + " ");
		}
	}
}