package me.jayng;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static Utils utils;
    public static DBI dbi = DBI.getInstance();
    public static Connection conn;
    public static Scanner scanner = new Scanner(System.in).useDelimiter("\\n");
	static int eid;
	static boolean isManager = false;
	static String today = "";

    public static void main(String[] args) {
	// write your code here
        printHeading();
        login();
        presentOption();
    }

    public static void printHeading() {
        System.out.println("--------------------------------------------------");
        System.out.println("------ Salon Software by Huy Nguyen, Man Vu ------");
        System.out.println("--------------------------------------------------");
    }

    public static void login() {
        Boolean success = false;
        String username = "";
        while (!success) {

            System.out.println("\nPlease Login");
            System.out.print("Username: ");
            username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.println();

            // Get String Digest with SHA256
            String digest = utils.sha256(password);

            // Construct Query for login process
            // Function return 1|0 (true|false)
            String loginCheckSQL = "SELECT f_loginCheck('" + username + "','" + digest + "');";
            Boolean matched = false;
            try {
                ResultSet rsLogin = dbi.executeStatement(loginCheckSQL);
                rsLogin.next();
                matched = rsLogin.getBoolean(1);
            } catch (SQLException e) {
                System.out.println("SQLException: " + e);
            }

            if (matched) {
                success = true;
				// get eid from username and password
				String getEID = "SELECT f_getEID('" + username + "','" + digest + "');";
				try {
					ResultSet rsEID = dbi.executeStatement(getEID);
					rsEID.next();
					eid = rsEID.getInt(1);
				} catch (SQLException e) {
					System.out.println("SQLException: " + e.getMessage());
				}
				// get isManager from username and password
				String checkManager = "SELECT f_getEmployeeType('" + username + "','" + digest + "');";
				try {
					ResultSet rsType = dbi.executeStatement(checkManager);
					rsType.next();
					isManager = rsType.getBoolean(1);
				} catch (SQLException e) {
					System.out.println("SQLException: " + e.getMessage());
				}
            } else {
                System.out.println("-> Login failed for " + username + ". Please check your username and password.\n-> If problem persists, please contact administrator.");
            }
        }

        if (isManager) {
            System.out.println("-> Welcome " + username + ", you have manager permission.");
			System.out.println();
        } else {
            System.out.println("-> Welcome " + username + ", you have employee permission.");
			System.out.println();
        }
    }

	// Present Manager Options
    public static void presentOption() {
    	if (isManager) {
			System.out.println("1. Manage Employees");
			System.out.println("2. Manage Appointments");
			System.out.println("3. Manage Products");
			System.out.println("4. View Customers");
			System.out.println("5. Services");
			System.out.println("6. Orders");
			System.out.println("7. End Period.");
			System.out.println("0. Exit");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					presentEmployeeOption();
					break;
				case 2:
					presentAppointmentOption();
					break;
				case 3:
					presentProductOption();
					break;
				case 4:
					// sql view customers
					System.out.println("All customers: ");
					viewAllCustomers();
					presentOption();
					break;
				case 5:
					presentServicesOption();
					break;
				case 6:
					presentOrderOption();
					break;
				case 7:
					System.out.print("WARNING: This will update YearToDatePay and reset all service counts. Continue? (Y/N)");
					String userInput = scanner.next();
					if (userInput.equalsIgnoreCase("Y")) {
						endPeriod();
						presentOption;
					}
					else {
						presentOption;
					}
					break;
				case 0:
					System.exit(1);
				default:
					System.out.println("Invalid option.");
					break;
			}
		} else {
			System.out.println("1. View your information");
			System.out.println("2. Manage Appointments");
			System.out.println("3. View your customers");
			System.out.println("4. Services");
			System.out.println("5. Orders");
			System.out.println("0. Exit");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					//View employee info if not manager
					viewSingleEmployee(eid);
					presentOption();
					break;
				case 2:
					presentAppointmentOption();
					break;
				case 3:
					// sql view
					System.out.println("My customers: ");
					viewYourCustomers(eid);
					presentOption();
					break;
				case 4:
					presentServicesOption();
					break;
				case 5:
					presentOrderOption();
					break;
				case 0:
					System.exit(1);
				default:
					System.out.println("Invalid option.");
					break;
			}
		}
    }

    // Present Employee Option
    public static void presentEmployeeOption() {
    	if (isManager) {
			System.out.println("1. View all employees");
			System.out.println("2. Add an employee");
			System.out.println("3. Delete an employee");
			System.out.println("0. Back");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			// To do: add sql statements to view, add, delete
			switch (option) {
				case 1:
					//View all employees
					System.out.println("All employees:");
					viewAllEmployees();
					presentEmployeeOption();
					break;

				case 2:
					//Add an employee
					addAnEmployee();
					presentEmployeeOption();
					break;

				case 3:
					//Delete an employee
					System.out.println("Enter the id of the employee you want to delete: ");
					int dID = scanner.nextInt();
					deleteAnEmployee(dID);
					presentEmployeeOption();
					break;
				case 0:
					presentOption();
					break;
				default:
					System.out.println("Invalid option.");
					break;
			}
		}
    }

    public static void presentAppointmentOption() {
    	if (isManager) {
			System.out.println("1. View today appointments");
			System.out.println("2. Add an appointment");
			System.out.println("3. Delete an appointment");
			System.out.println("4. Edit an appointment");
			System.out.println("9. View all appointments");
			System.out.println("0. Back");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					viewTodayAppointment();
					presentAppointmentOption();
					break;
				case 2:
					addAnAppointment();
					presentAppointmentOption();
					break;
				case 3:
					deleteAnAppointment();
					presentAppointmentOption();
					break;
				case 4:
					editAnAppointment();
					presentAppointmentOption();
					break;
				case 9:
					viewAllAppointment();
					presentAppointmentOption();
					break;
				case 0:
					presentOption();
					break;
				default:
					System.out.println("-> Invalid Option.");
			}
		} else {
			System.out.println("1. View today appointments");
			System.out.println("2. Add an appointment");
			System.out.println("3. Delete an appointment");
			System.out.println("4. Edit an appointment");
			System.out.println("0. Back");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					viewTodayAppointment();
					presentAppointmentOption();
					break;
				case 2:
					addAnAppointment();
					presentAppointmentOption();
					break;
				case 4:
					editAnAppointment();
					presentAppointmentOption();
					break;
				case 0:
					presentOption();
					break;
				default:
					System.out.println("-> Invalid Option.");
			}
		}
		// sql statements for appointments
	}

	public static void presentProductOption() {
    	if (isManager) {
			System.out.println("1. View all products");
			System.out.println("2. Add a products");
			System.out.println("3. Edit a product");
			System.out.println("4. Delete a product");
			System.out.println("0. Back");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					viewAllProduct();
					presentProductOption();
					break;
				case 2:
					addAProduct();
					presentProductOption();
					break;
				case 3:
					editAProduct();
					presentProductOption();
					break;
				case 4:
					deleteAProduct();
					presentProductOption();
					break;
				case 0:
					presentOption();
					break;
				default:
					System.out.println("-> Invalid Option.");
			}
		}
	}

	public static void presentServicesOption() {
		if (isManager) {
			System.out.println("1. View services");
			System.out.println("2. Add a service");
			System.out.println("3. Edit a service");
			System.out.println("4. Delete a service");
			System.out.println("5. View employees' service counts");
			System.out.println("6. Update employees' service counts");
			System.out.println("0. Back");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					viewAllServices();
					presentServicesOption();
					break;
				case 2:
					addAService();
					presentServicesOption();
					break;
				case 3:
					editAService();
					presentServicesOption();
					break;
				case 4:
					deleteAService();
					presentServicesOption();
					break;
				case 5:
					viewAllEmployeesServiceCounts();
					presentServicesOption();
					break;
				case 6:
					updateServicesCount();
					presentServicesOption();
					break;
				case 0:
					presentOption();
					break;
				default:
					System.out.println("-> Invalid Option.");
			}
		} else {
			System.out.println("1. View all offered services");
			System.out.println("2. View your service counts");
			System.out.println("3. New service count");
			System.out.println("4. Update your service counts");
			System.out.println("0. Back");
			System.out.println();
			System.out.print("- Choice: ");
			int option = Utils.getInput();
			switch (option) {
				case 1:
					viewAllServices();
					presentServicesOption();
					break;
				case 2:
					viewPersonalServiceCounts(eid);
					presentServicesOption();
					break;
				case 3:
					empAddServiceCount();
					presentServicesOption();
					break;	
				case 4:
					empUpdateCount();
					presentServicesOption();
					break;
				case 0:
					presentOption();
					break;
				default:
					System.out.println("-> Invalid Option.");
			}
		}
	}

	public static void presentOrderOption() {
	
		System.out.println("1. View all orders");
		System.out.println("2. Place an order");
		System.out.println("3. Edit an order");
		System.out.println("4. Cancel an order");
		System.out.println("0. Back");
		System.out.println();
		System.out.print("- Choice: ");
		int option = Utils.getInput();
		switch (option) {
			case 1:
				viewOrders();
				presentOrderOption();
				break;
			case 2:
				placeOrder();
				presentOrderOption();
				break;
			case 3:
				editOrder();
				presentOrderOption();
				break;
			case 4:
				cancelOrder();
				presentOrderOption();
				break;
			case 0:
				presentOption();
				break;
			default:
				System.out.println("-> Invalid Option.");
		}
		
	}
	//Manager's view
	public static void viewAllEmployees() {
		String viewEmployees = "CALL view_all_emp();";
        ResultSet rsView = dbi.executeStatement(viewEmployees);
        Utils.printResultSet(rsView);
	}

	//Employee's view
	public static void viewSingleEmployee(int id){
		String viewOneEmployee = "CALL view_an_employee(" + id + ");";
        ResultSet rsView = dbi.executeStatement(viewOneEmployee);
        Utils.printResultSet(rsView);
	}
	
	//Insert an employee
	public static void addAnEmployee() {
		System.out.println("Employee's name: ");
		String name = scanner.next();
		
		System.out.println("Employee's date of birth (format YYYY-MM-DD): ");
		String dob = Utils.getDate();
		
		System.out.println("Employee's SSN: ");
		String ssn = scanner.next();
		
		System.out.println("Employee's address: ");
		String address = scanner.next();
		
		System.out.println("Employee's phone number: ");
		String phone = scanner.next();
		
		System.out.println("Employee's username: ");
		String user = scanner.next();
		
		System.out.println("Employee's password: ");
		String pw = scanner.next();
		String digest = Utils.sha256(pw);
		
		System.out.println("Is this employee a manager? (y for yes, otherwise no) ");
		String man = scanner.next();
		int manager = 0;
		if(man.equalsIgnoreCase("y")){
			manager = 1;
		}
		
		// sql statement
		String insert = "CALL insert_emp('" + name + "', '" + dob + "', '" + ssn + "', '"
		+ address + "', '" + phone + "', '" + user + "', '" + digest + "', " + manager + ");";

        dbi.executeStatement(insert);
        System.out.println("-> Inserted an employee");
	}

	//Delete an employee
	public static void deleteAnEmployee(int dID) {
		String deleteEmp = "CALL delete_an_emp(" + dID + ");";
        dbi.executeStatement(deleteEmp);
        System.out.println("-> Deleted employee "+ dID);
	}
	
	// View all customers
	public static void viewAllCustomers(){
		String allCustomers = "CALL view_all_customers();";
        ResultSet rsView = dbi.executeStatement(allCustomers);
        Utils.printResultSet(rsView);
	}
	
	// View your customers
	public static void viewYourCustomers(int id){
		String customers = "CALL view_customers(" + id + ");";
        ResultSet rsView = dbi.executeStatement(customers);
        Utils.printResultSet(rsView);
	}

	// View All Appointments
	public static void viewAllAppointment() {
		String getAptSQL = "CALL view_all_appointments;";
		ResultSet rsView = dbi.executeStatement(getAptSQL);
		Utils.printResultSet(rsView);
	}

	// View Today Appointments (Should be used the most for normal operations)
	public static void viewTodayAppointment() {
        String getAptSQL = "CALL view_today_appointments;";
        ResultSet rsView = dbi.executeStatement(getAptSQL);
        Utils.printResultSet(rsView);
    }

    // View your appointment
    public static void viewYourAppointment() {
	    String getYourAptSQL = "CALL view_emp_appointments(" + eid + ");";
        ResultSet yourApt = dbi.executeStatement(getYourAptSQL);
	    Utils.printResultSet(yourApt);
    }

    // Add an appointment for an employee
	public static void addAnAppointment() {
		viewAllEmployees();
		System.out.println("Please enter Employee ID. You can refer to the table above for correct ID: ");
		int id = Utils.getId();
		System.out.println("Please enter the date of the appointment (Format YYYY-MM-DD): ");
		String date = Utils.getDate();
		String getEmpAptSQL = "CALL view_emp_appointments(" + id + ", '" + date +"');";
        ResultSet empApt = dbi.executeStatement(getEmpAptSQL);
		Utils.printResultSet(empApt);
		System.out.println("Please enter start time (Format: HH:MM): ");
		String startTime = Utils.getTime();
		boolean conflict = true;
		while (conflict) {
            String aptCheckSQL = "SELECT f_appointmentCheck(" + id + ", '" + date + "', '" + startTime + "');";
            try {
		        ResultSet aptCheck = dbi.executeStatement(aptCheckSQL);
		        if (aptCheck.next()) {
                    conflict = aptCheck.getBoolean(1);
                } else {
		            conflict = false;
                }
		        if (conflict) {
		            System.out.println("-> Employee already have an appointment running at " + startTime + ".");
                    System.out.println("Please reschedule with a new start time (Format: HH:MM): ");
                    startTime = Utils.getTime();
                }
            } catch (SQLException e) {
		        System.out.println("SQLException: " + e.getMessage());
            }
        }
        System.out.println("Please enter service name: ");
		String serviceName = scanner.next();
		System.out.println("Please enter customer name: ");
		String customerName = scanner.next();
		String getEndTimeSQL = "SELECT f_getEndTime('" + startTime + "', '" + serviceName + "');";
		String endTime = "";
		try {
		    ResultSet getEndTime = dbi.executeStatement(getEndTimeSQL);
		    getEndTime.next();
		    endTime = getEndTime.getString(1);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        String addEmpAptSQL = "CALL add_emp_appointment(" + id + ", '" + startTime + "', '" + endTime + "', '" + date + "', '" + serviceName + "', '" + customerName + "');";
		dbi.executeStatement(addEmpAptSQL);
		System.out.println("-> Appointment for " + customerName + " at " + startTime + " on " + date + " Added");
	}

	public static void deleteAnAppointment() {
		if (isManager) {
			viewAllAppointment();
			System.out.print("Please enter employee id: ");
			int id = Utils.getId();
			System.out.print("Please enter date (Format: YYYY-MM-DD): ");
			String date = Utils.getDate();
			System.out.print("Please enter start time (Format: HH:MM): ");
			String startTime = Utils.getTime();
			try {
				String delAptCheckSQL = "SELECT f_deleteAptCheck(" + id + ", '" + startTime + "', '" + date + "');";
				ResultSet delAptCheck = dbi.executeStatement(delAptCheckSQL);
				delAptCheck.next();
				boolean exists = delAptCheck.getBoolean(1);
				if (exists) {
					String deleteAptSQL = "CALL del_emp_appointment(" + id + ", '" + startTime + "', '" + date + "');";
					dbi.executeStatement(deleteAptSQL);
				}
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			}
		} else {
			viewYourAppointment();
			System.out.print("Please enter date (Format: YYYY-MM-DD): ");
			String date = Utils.getDate();
			System.out.print("Please enter start time (Format: HH:MM): ");
			String startTime = Utils.getTime();
			try {
				String delAptCheckSQL = "SELECT f_deleteAptCheck(" + eid + "', '" + startTime + "', '" + date + "');";
				ResultSet delAptCheck = dbi.executeStatement(delAptCheckSQL);
				delAptCheck.next();
				boolean exists = delAptCheck.getBoolean(1);
				if (exists) {
					String deleteAptSQL = "CALL del_emp_appointment(" + eid + "', '" + startTime + "', '" + date + "');";
					dbi.executeStatement(deleteAptSQL);
				}
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			}
		}
	}

	public static void editAnAppointment() {
		if (isManager) {
			viewAllAppointment();
			System.out.print("Please enter employee id: ");
			int id = Utils.getId();
			System.out.print("Please enter date (Format: YYYY-MM-DD): ");
			String date = Utils.getDate();
			System.out.print("Please enter start time (Format: HH:MM): ");
			String startTime = Utils.getTime();
			try {
				String delAptCheckSQL = "SELECT f_deleteAptCheck(" + id + ", '" + startTime + "', '" + date + "');";
				ResultSet delAptCheck = dbi.executeStatement(delAptCheckSQL);
				delAptCheck.next();
				boolean exists = delAptCheck.getBoolean(1);
				if (exists) {
					System.out.print("Please enter new employee for this appointment, enter the same one if not changed: ");
					int newId = Utils.getId();
					System.out.print("Please enter new date, enter the same if not changed: ");
					String newDate = Utils.getDate();
					System.out.print("Please enter new start time, enter the same if not changed: ");
					String newStartTime = Utils.getTime();
					System.out.print("Please enter new service, enter the same if not changed: ");
					String newService = scanner.next();
					System.out.print("Please enter new customer name, enter the same if not changed: ");
					String newCustomer = scanner.next();
					String updateAptSQL = "CALL update_emp_appointment(" + id + ", '" + startTime + "', '" + date + "', " + newId + ", '" + newStartTime + "', '" + newDate + "', '" + newService + "', '" + newCustomer + "');";
					dbi.executeStatement(updateAptSQL);
				}
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			}
		} else {
			viewYourAppointment();
			System.out.print("Please enter date (Format: YYYY-MM-DD): ");
			String date = Utils.getDate();
			System.out.print("Please enter start time (Format: HH:MM): ");
			String startTime = Utils.getTime();
			try {
				String delAptCheckSQL = "SELECT f_deleteAptCheck(" + eid + ", '" + startTime + "', '" + date + "');";
				ResultSet delAptCheck = dbi.executeStatement(delAptCheckSQL);
				delAptCheck.next();
				boolean exists = delAptCheck.getBoolean(1);
				if (exists) {
					System.out.print("Please enter new date, enter the same if not changed: ");
					String newDate = Utils.getDate();
					System.out.print("Please enter new start time, enter the same if not changed: ");
					String newStartTime = Utils.getTime();
					System.out.print("Please enter new service, enter the same if not changed: ");
					String newService = scanner.next();
					System.out.print("Please enter new customer name, enter the same if not changed: ");
					String newCustomer = scanner.next();
					String updateAptSQL = "CALL update_emp_appointment(" + eid + ", '" + startTime + "', '" + date + "', " + eid + ", '" + newStartTime + "', '" + newDate + "', '" + newService + "', '" + newCustomer + "');";
					dbi.executeStatement(updateAptSQL);
				}
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			}
		}
	}

	public static void viewAllProduct() {
		String products = "CALL view_products();";
		ResultSet rsView = dbi.executeStatement(products);
		utils.printResultSet(rsView);
	}

	public static void addAProduct() {
		
		System.out.println("Enter product name: ");
		String name = scanner.next();
		
		System.out.println("Enter product type: ");
		String type = scanner.next();
		
		System.out.println("Enter product amount: ");
		int amount = scanner.nextInt();
		
		System.out.println("Enter product price: ");
		int price = scanner.nextInt();
		
		String addProduct = "CALL add_product('" + name + "', '" + type + "', " + amount + ", " +  price + ");";
        dbi.executeStatement(addProduct);
        System.out.println("Product added.");
		
	}

	public static void editAProduct() {
		System.out.println("Edit a product.");
		System.out.println("Enter product code: ");
		int code = scanner.nextInt();
		
		System.out.println("Enter product new name: ");
		String name = scanner.next();
		
		System.out.println("Enter product new type: ");
		String type = scanner.next();
		
		System.out.println("Enter product new amount: ");
		int amount = scanner.nextInt();
		
		System.out.println("Enter product new price: ");
		int price = scanner.nextInt();
		
		String editProduct = "CALL add_product(" + code +", '" + name + "', '" + type + "', " + amount + ", " +  price + ");";
        dbi.executeStatement(editProduct);
        System.out.println("Edited the product.");
	}

	public static void deleteAProduct() {
		System.out.println("Enter product code to delete: ");
		int code = scanner.nextInt();
		String deleteProduct = "CALL delete_product(" + code + ");";
        dbi.executeStatement(deleteProduct);
        System.out.println("Deleted the product.");
	}

	public static void viewAllServices() {
		String allServices = "CALL view_all_services();";
		ResultSet rsView = dbi.executeStatement(allServices);
		utils.printResultSet(rsView);
	}

	public static void addAService() {
		System.out.println("Enter service name: ");
		String name = scanner.next();
		
		System.out.println("Enter service price: ");
		int price = scanner.nextInt();
		
		System.out.println("Enter service duration (Format HH:MM): ");
		String time = Utils.getTime();
		
		String addService = "CALL add_service('" + name + "', " + price + ", '" + time + "');";
		dbi.executeStatement(addService);
		System.out.println("Service added.");
	}

	public static void editAService() {
		System.out.println("Enter service name: ");
		String name = scanner.next();
		
		System.out.println("Enter new service price: ");
		int price = scanner.nextInt();
		
		System.out.println("Enter new service duration (format HH:MM): ");
		String time = Utils.getTime();
		
		String editService = "CALL edit_a_service('" + name + "', " + price + ", '" + time + "');";
		dbi.executeStatement(editService);
		System.out.println("Service edited.");
	}

	public static void deleteAService() {
		System.out.println("Enter service name you want to remove:");
		String rm = scanner.next();
		String deleteService = "CALL delete_a_service('" + rm + "');";
		dbi.executeStatement(deleteService);
		System.out.println("Service deleted");
	}
	// View all orders
	public static void viewOrders() {
		String allOrders = "CALL view_all_orders();";
		ResultSet rsView = dbi.executeStatement(allOrders);
		utils.printResultSet(rsView);
	}
	
	public static void placeOrder() {
		System.out.println("Enter customer name: ");
		String name = scanner.next();
		
		System.out.println("Enter customer's phone number: ");
		int phone= scanner.nextInt();
		
		System.out.println("Enter employee's id: ");
		int id = scanner.nextInt();
		
		System.out.println("Enter product code: ");
		int product = scanner.nextInt();
		
		System.out.println("Enter amount: ");
		int amount = scanner.nextInt();
		
		String placeOrder = "CALL place_order('" + name + "', '" + phone + "', " + id +", "+ product+ ", "+amount + ");";
		dbi.executeStatement(placeOrder);
		System.out.println("Order placed.");
	}
	
	public static void editOrder() {
		System.out.println("Enter order number: ");
		int orderNo = scanner.nextInt();
		
		System.out.println("Enter new product code: ");
		int product = scanner.nextInt();
		
		System.out.println("Enter new amount: ");
		int amount = scanner.nextInt();
		
		String editOrder = "CALL edit_order(" + orderNo + ", " + product + ", " + amount + ");";
		dbi.executeStatement(editOrder);
		System.out.println("Order was changed.");
				
	}
	
	public static void cancelOrder() {
		System.out.println("Enter order number: ");
		int orderNo = scanner.nextInt();
		
		String deleteOrder = "CALL delete_order(" + orderNo +");";
		dbi.executeStatement(deleteOrder);
		System.out.println("Order was cancelled.");
	}
	
	//For manager
	public static void viewAllEmployeesServiceCounts() {
		String empSevCount = "CALL view_all_employees_service_counts();";
		ResultSet rsView = dbi.executeStatement(empSevCount);
		utils.printResultSet(rsView);
	}

	//For employee
	public static void viewPersonalServiceCounts(int id) {
		String pSevCount = "CALL view_personal_counts(" + id + ");";
		ResultSet rsView = dbi.executeStatement(pSevCount);
		utils.printResultSet(rsView);
	}
	
	//For manager
	public static void addServicesCount() {
		System.out.println("Enter employee id: ");
		int id = Utils.getId();
		System.out.println("Enter service name: ");
		String service = scanner.next();
		System.out.println("Enter service count: ");
		int count = scanner.nextInt();
		
		String addNewCount = "CALL add_new_count(" + id + ", '" + service + "', " + count + ");";
		dbi.executeStatement(addNewCount);
		System.out.println("Added new count for employee " + id);
	}

	public static void updateServicesCount() {
		System.out.println("Enter employee id: ");
		int id = Utils.getId();
		System.out.println("Enter service name: ");
		String service = scanner.next();
		System.out.println("Enter new service count: ");
		int count = scanner.nextInt();
		
		String updateCount = "CALL update_count(" + id + ", '" + service + "', " + count + ");";
		dbi.executeStatement(updateCount);
		System.out.println("Updated count for employee " + id + ", service " + service);
	}
	
	//For employee
	//Employees can only add new service with a count of 1 when it is the their first time
	public static void empAddServiceCount() {
		System.out.println("Use this if the service doesn't exist yet");
		System.out.println("Enter service name: ");
		String service = scanner.next();
		String addNewCount = "CALL add_new_count(" + eid + ", '" + service + "', " + 1 + ");";
		dbi.executeStatement(addNewCount);
		System.out.println("Added new count for employee " + eid);
	}
	//This is called when an employee finishes his/her service, thus count increases by 1
	public static void empUpdateCount() {
		System.out.println("Enter service name: ");
		String service = scanner.next();
		
		String empUpdateCount = "CALL update_count_emp_ver(" + eid + ", '" + service + "');";
		dbi.executeStatement(empUpdateCount);
		System.out.println("Updated count for employee " + eid + ", service " + service);
	}
	
	//Manager only
	//End the period, calculate year to date pay and reset counts
	public static void endThePeriod() {
		String endPeriod = "CALL end_period();";
		dbi.executeStatement(endPeriod);
		System.out.println("Updated yearToDatePay, reset all service counts and periodPay");
	}
}
