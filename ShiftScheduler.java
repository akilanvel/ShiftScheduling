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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// TODO: if no other slot available, some employees work the same shift as their 2 shifts (Akilan and Akshitha)

public class ShiftScheduler {
	private Employee[] employees; // Holds list of employees (sorted by increasing availability)
	private Employee[][] shiftAssignments; // Holds all shift assignments (35 x 2)
	private int[] listOfShifts; // Holds shift indexes from 0 to 34 (35 total)
	private HashMap<Integer, String> shiftTitles; // Holds readable String corresponding to each of the 35 shift
													// integers 0-34
	private int numUnassigned; // Holds no. of unassigned employees (due to full shifts and limited
								// availability)
	private String unAssigned; // Holds list of unassigned employees in String form

	// Constructor
	public ShiftScheduler(Employee[] e) {
		// Initialize instance variables
		employees = e;
		shiftAssignments = new Employee[35][2];
		listOfShifts = new int[35];
		shiftTitles = new HashMap<>();
		numUnassigned = 0;
		unAssigned = "";

		// Populate the listOfShifts array with corresponding values
		for (int i = 0; i < listOfShifts.length; i++) {
			listOfShifts[i] = i;
		}

		// Populate the shiftAssignments 2D array with null employees at each shift
		for (int i = 0; i < shiftAssignments.length; i++) {
			for (int x = 0; x < shiftAssignments[i].length; x++) {
				shiftAssignments[i][x] = null;
			}
		}

		// Populate the shiftTitles HashMap with String corresponding to each shift
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
			// Assign one or two shifts for the employee, based on rank
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
			} else {
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
					unAssigned += "Unable to assign shift for " + employee.getName() + "\n";
					numUnassigned++;
				}
			}
		}
	}

	// This method returns the list of unassigned employees
	public String getUnAssigned() {
		return unAssigned;
	}

	// This method returns the number of unassigned employees
	public int numUnassigned() {
		return numUnassigned;
	}

	// This method specifies whether an employee has an open shift in their
	// availability that they aren't already a part of
	private boolean hasAvailableShift(Employee employee) {
		if (employee == null)
			return false;

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
			// FYI: this comparison won't work if there are two employees with the same full
			// name
			if (shiftAssignments[shift][i] != null) {
				if (shiftAssignments[shift][i].getName().equals(employee.getName())) {
					// Remove employee from the shift in the shiftAssignments 2D array
					shiftAssignments[shift][i] = null;
					// Mark the employee is available for the shift (since they were just removed)
					employee.makeAvailable(shift);
				}
			}
		}
	}

	// This method adds an employee to a shift
	private void addToShift(int shift, Employee employee) {
		for (int i = 0; i < shiftAssignments[shift].length; i++) {
			if (shiftAssignments[shift][i] == null) {
				// Add employee to the shift in the shiftAssignments 2D array
				shiftAssignments[shift][i] = employee;
				// Mark the employee as unavailable for the shift (since they were just added)
				employee.makeUnavailable(shift);
				break;
			}
		}
	}

	// This method finds the next open shift in the employee's availability
	// It prioritizes finding empty shifts first (to spread out shifts)
	private int findAvailableShift(Employee employee) {
		// First see if there are empty shifts
		for (int shift : listOfShifts) {
			if (employee.getAvailability().contains(shift) && employee.isAvailable(shift)
					&& numEmployeesOnShift(shift) == 0) {
				return shift;
			}
		}
		// Only if there are no empty shifts, check non-empty shifts
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
		System.out.println("Full Schedule:");
		for (int shift : listOfShifts) {
			System.out.print(shiftTitles.get(shift) + ":\t");
			for (Employee employee : shiftAssignments[shift]) {
				if (employee != null) {
					System.out.print(employee + ", ");
				}
			}
			System.out.println();
		}
		System.out.println(unAssigned);
	}

	public static void main(String[] args) {
		System.out.println("------------------------------------------");
		System.out.println("Shift Scheduling Application");
		System.out.println("------------------------------------------");
		System.out.println("Author: Akilan Gnanavel");
		System.out.println("Made for UEMR");
		System.out.println("------------------------------------------\n\n");

		// Read in all employees from the input file
		ArrayList<String> inputs = readLines("original.txt"); // <-- NAME OF INPUT FILE GOES HERE
		Employee[] unmatched = new Employee[inputs.size()];
		for (int i = 0; i < inputs.size(); i++) {
			unmatched[i] = new Employee(inputs.get(i));
		}

		// Split employees into lists based on rank
		ArrayList<Employee> emt1 = new ArrayList<>();
		ArrayList<Employee> emt2 = new ArrayList<>();
		ArrayList<Employee> fto = new ArrayList<>();
		for (Employee e : unmatched) {
			if (e.getType().equals("EMT-1")) {
				emt1.add(e);
			} else if (e.getType().equals("EMT-2")) {
				emt2.add(e);
			} else if (e.getType().equals("FTO")) {
				fto.add(e);
			}
		}

		// Execute the program only if there are an equal number of EMT-1s and FTOs
		if (emt1.size() != fto.size()) {
			System.out.println("Error: cannot generate schedule");
			System.out.println("The number of FTOs and EMT-1s are not equal");
		} else {
			int numUnassigned = 0;
			ShiftScheduler shiftScheduler = new ShiftScheduler(new Employee[2]);
			ArrayList<Employee> pairs = new ArrayList<>();
			System.out.println("Running...\n\n");

			// Call the function to generate feasible EMT-1 and FTO pairs
			pairs = generateMatchings(emt1, fto);

			// Add EMT-2s and EMT-1/FTO pairs into one Employee array
			Employee[] employees = new Employee[emt2.size() + pairs.size()];
			for (int x = 0; x < emt2.size(); x++) {
				employees[x] = emt2.get(x);
			}
			for (int x = 0; x < pairs.size(); x++) {
				employees[x + emt2.size()] = pairs.get(x);
			}

			// Sort the employees by increasing availability to simplify scheduling
			employees = sortByAvailability(employees);

			// Create ShiftScheduler object
			shiftScheduler = new ShiftScheduler(employees);
			shiftScheduler.scheduleShifts(); // Assign all the shifts
			numUnassigned = shiftScheduler.numUnassigned();

			// Let the user know whether a full schedule could be generated
			if (numUnassigned == 0) {
				System.out.println("A full schedule was generated using the availabilities:\n\n");
			} else {
				System.out.println("After running the program, a full schedule could not be generated");
				System.out.println("EMTs with unassigned shifts are listed at the bottom");
				System.out.println("You may run the program again to generate a different schedule\n\n");
			}
			// Print out the EMT-1/FTO pairs that were generated
			System.out.println("FTO and EMT-1 Pairs:");
			System.out.println("Pairs generated: " + pairs.size() + "\n");
			for (Employee e : pairs) {
				System.out.println(e);
			}
			System.out.println("\n");

			// Print out the schedule that was generated
			shiftScheduler.printSchedule();
		}
	}

	// This method generates a list of pairings between EMT-1s and FTOs
	// Each pair in the list of pairings has at least 2 shared availabilities
	public static ArrayList<Employee> generateMatchings(ArrayList<Employee> emt1, ArrayList<Employee> fto) {
		ArrayList<Employee> pairs = new ArrayList<>();

		// Temporary employee list copies
		Employee[] emt1copy = emt1.toArray(new Employee[emt1.size()]);
		Employee[] ftocopy = fto.toArray(new Employee[fto.size()]);

		// Loop through EMT-1s and compare each to the FTOs
		for (Employee e1 : emt1copy) {
			for (Employee e2 : ftocopy) {
				ArrayList<Integer> combinedAvailability = combineAvailability(e1, e2);
				if (combinedAvailability.size() >= 2) { // If the pair has at least two shared availabilities:
					// Confirm the pair and remove them from their respective list
					// (Because each employee can only be in one pair)
					pairs.add(new Employee(
							e2.getName().substring(0, e2.getName().indexOf(" ")) + " and "
									+ e1.getName().substring(0, e1.getName().indexOf(" ")),
							"Pair", combinedAvailability));
					emt1copy = remove(emt1copy, e1);
					ftocopy = remove(ftocopy, e2);
					break;
				}
			}
		}

		// If not all employees could be assigned a pair, try again with diff. order
		// But if all employees were assigned a pair, these are the parings to return
		if (pairs.size() < emt1.size()) {
			return generateMatchings(scramble(emt1), scramble(fto));
		} else {
			return pairs;
		}
	}

	// This method removes an employee from an array of employees
	public static Employee[] remove(Employee[] e, Employee e1) {
		List<Employee> list = new ArrayList<>(Arrays.asList(e));
		list.remove(e1);
		return list.toArray(new Employee[0]);
	}

	// This method scrambles the order of an ArrayList of employees
	public static ArrayList<Employee> scramble(ArrayList<Employee> employees) {
		Collections.shuffle(employees);
		return employees;
	}

	// This method finds the shared availability between two employees
	// It returns a list of the shared availabilities
	public static ArrayList<Integer> combineAvailability(Employee e1, Employee e2) {
		ArrayList<Integer> combined = new ArrayList<>();
		for (int shift1 : e1.getAvailability()) {
			if (e2.getAvailability().contains(shift1)) {
				combined.add(shift1);
			}
		}
		return combined;
	}

	// This method returns the number of shared availabilities between 2 employees
	public static int sharedAvailability(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		int count = 0;
		for (int i : list1) {
			if (list2.contains(i)) {
				count++;
			}
		}
		return count;
	}

	// This method reads the input from a specified file
	// It returns the lines as an ArrayList of Strings
	public static ArrayList<String> readLines(String fileName) {
		ArrayList<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			System.out.println("Error while reading input file");
		}
		return lines;
	}

	// This method sorts an array of Employees by increasing availability
	public static Employee[] sortByAvailability(Employee[] employees) {
		Arrays.sort(employees, (e1, e2) -> e1.getAvailability().size() - e2.getAvailability().size());
		return employees;
	}
}

class Employee {
	private String type; // Holds the employee's type (or whether it's a pair)
	private String name; // Holds the employee or pair's name
	private ArrayList<Integer> availability; // Holds employee's (or pair's) available shifts
	private ArrayList<Boolean> active; // FALSE MEANS EMPLOYEE IS OPEN TO WORK AT THAT SHIFT, TRUE MEANS NOT

	// Constructor
	public Employee(String input) {
		// Assign the employee's instance variables based on the input line format
		// FORMAT IS AS FOLLOWS: "Akilan Gnanavel FTO 0 7 12 34"
		String[] parameters = input.split(" ");
		type = parameters[2];
		name = parameters[0] + " " + parameters[1];
		availability = new ArrayList<>();
		active = new ArrayList<>();

		// Assign the inputted availability to the employee (if its between 0-34)
		if (this.type.equals("EMT-1") || this.type.equals("EMT-2") || this.type.equals("FTO")) {
			for (int i = 3; i < parameters.length; i++) {
				try {
					int avail = Integer.parseInt(parameters[i]); // Holds current availability to add
					if (avail < 0 || avail > 34) {
						System.out.println(
								this.name + " has an invalid availability: " + Integer.parseInt(parameters[i]));
						System.out.println("This invalid availability was ignored when assigning shifts\n\n");
					} else {
						availability.add(avail);
						active.add(false); // The employee isn't working any shift yet, so every availability is false
					}
				} catch (Exception e) {
					System.out.println(this.name + " has an invalid availability: " + parameters[i]);
					System.out.println("This invalid availability was ignored when assigning shifts\n\n");
				}
			}
		} else {
			System.out.println(this.name + " has an invalid rank of: " + this.type);
			System.out.println("They were not assigned any shifts\n\n");
		}
	}

	// Constructor
	public Employee(String name, String type, ArrayList<Integer> availability) {
		this.name = name;
		this.type = type;
		this.availability = availability;
		this.active = new ArrayList<>();
		for (int i = 0; i < availability.size(); i++) {
			this.active.add(false);
		}
	}

	// Getter method for type
	public String getType() {
		return type;
	}

	// Getter method for name
	public String getName() {
		return name;
	}

	// Getter method for availability
	public ArrayList<Integer> getAvailability() {
		return availability;
	}

	// This method marks an employee as unavailable for a shift
	// This is to be used when assigning an employee to that shift
	public void makeUnavailable(int shift) {
		active.set(availability.indexOf(shift), true);
	}

	// This method marks an employee as available for a shift
	// This is to be used when removing an employee from that shift
	public void makeAvailable(int shift) {
		active.set(availability.indexOf(shift), false);
	}

	// This method specified whether an employee is scheduled to work at a shift
	public boolean isAvailable(int shift) {
		return !active.get(availability.indexOf(shift));
	}

	// toString method for printing
	@Override
	public String toString() {
		return this.name;
	}
}