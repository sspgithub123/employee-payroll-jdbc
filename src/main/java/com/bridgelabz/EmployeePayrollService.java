package com.bridgelabz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {
    public enum IOService {
        CONSOLE_IO, FILE_IO, DB_IO, REST_IO
    }

    /**
     * To get the list of employee payroll from the database
     */
    public List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService() {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public static void main(String[] args) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeeData(consoleInputReader);
        employeePayrollService.writeEmployeeData(IOService.CONSOLE_IO);
    }

    /**
     * @param consoleInputReader Read employee data
     */
    public void readEmployeeData(Scanner consoleInputReader) {
        System.out.println("Enter employee ID : ");
        int id = Integer.parseInt(consoleInputReader.nextLine());
        System.out.println("Enter employee name : ");
        String name = consoleInputReader.nextLine();
        System.out.println("Enter employee salary : ");
        double salary = Double.parseDouble(consoleInputReader.nextLine());
        employeePayrollList.add(new EmployeePayrollData(id, name, salary));
    }

    /**
     * Write payroll data to console
     */
    public void writeEmployeeData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            System.out.println("Writing Employee Payroll Data to Console\n" + employeePayrollList);
        else if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
    }

    /**
     * @param ioService Print Data
     */
    public void printData(IOService ioService) {
        new EmployeePayrollFileIOService().printData();
    }

    /**
     * @param ioService
     * @return number of entries
     */
    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }

    /**
     * @param ioService
     * @return Employee Payroll Data List
     */
    public List<EmployeePayrollData> readData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().readData();
        else if (ioService.equals(IOService.DB_IO)) {
            employeePayrollList = employeePayrollDBService.readData();
            return employeePayrollList;
        } else
            return null;
    }

    /**
     * updating EmployeeSalary from the database
     *
     * @param name
     * @param salary
     * @throwsEmployeePayrollException
     */
    public void updateEmployeeSalary(String name, double salary, EmployeePayrollDBService.StatementType type) throws EmployeePayrollException {
        int result = employeePayrollDBService.updateEmployeeData(name, salary, type);
        EmployeePayrollData employeePayrollData = null;
        if (result == 0)
            throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.UPDATE_FAIL, "Update Failed");
        else
            employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null) {
            employeePayrollData.salary = salary;
        }
    }

    /**
     * getting the employeepayroll data
     *
     * @param name
     * @return Employee corresponding to name
     */
    private EmployeePayrollData getEmployeePayrollData(String name) {
        EmployeePayrollData employeePayrollData = this.employeePayrollList.stream()
                .filter(employee -> employee.name.equals(name)).findFirst().orElse(null);
        return employeePayrollData;
    }

    /**
     * method to check payroll is synced with databse
     *
     * @param name
     * @return true if data is in sync
     */
    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> checkList = employeePayrollDBService.getEmployeePayrollData(name);
        return checkList.get(0).equals(getEmployeePayrollData(name));

    }

    /**
     * Retrieve the data for a particular date range
     *
     * @param date1
     * @param date2
     * @return
     */
    public List<EmployeePayrollData> getEmployeesInDateRange(String date1, String date2) {
        List<EmployeePayrollData> employeesInGivenDateRangeList = employeePayrollDBService
                .getEmployeesInGivenDateRangeDB(date1, date2);
        return employeesInGivenDateRangeList;
    }

    /**
     * read the Average Salary group ByGender using Hashmap
     *
     * @param ioService
     * @return
     */
    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }
}
