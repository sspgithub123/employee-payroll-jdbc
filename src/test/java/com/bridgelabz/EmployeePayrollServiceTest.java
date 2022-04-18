package com.bridgelabz;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmployeePayrollServiceTest {
    @Test
    public void given3Employees_WhenWrittenToFile_ShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmp = {new EmployeePayrollData(1, "BILL", 100000.0),
                new EmployeePayrollData(2, "Terisa", 200000.0),
                new EmployeePayrollData(3, "Charlie", 300000.0)};
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmp));
        employeePayrollService.writeEmployeeData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        List<EmployeePayrollData> employeeList = employeePayrollService.readData(EmployeePayrollService.IOService.FILE_IO);
        System.out.println(employeeList);
        assertEquals(3, entries);
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readData(EmployeePayrollService.IOService.DB_IO);
        assertEquals(3, employeePayrollData.size());
    }
    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDatabase() throws EmployeePayrollException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00, EmployeePayrollDBService.StatementType.STATEMENT);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        assertTrue(result);
        System.out.println(employeePayrollData);
    }

    @Test
    /**
     * To test whether the salary is updated in the database and is synced with the
     * DB using JDBC PreparedStatement
     *
     * @throws EmployeePayrollException
     */
    public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDatabase()
            throws EmployeePayrollException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00, EmployeePayrollDBService.StatementType.PREPARED_STATEMENT);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        assertTrue(result);
        System.out.println(employeePayrollData);
    }
    @Test
    /**
     * To test whether the count of the retrieved data who have joined in a
     * particular data range matches with the expected value
     *
     * @throws EmployeePayrollException
     */
    public void givenDateRangeForEmployee_WhenRetrievedUsingStatement_ShouldReturnProperData()
            throws EmployeePayrollException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readData(EmployeePayrollService.IOService.DB_IO);
        List<EmployeePayrollData> employeeDataInGivenDateRange = employeePayrollService
                .getEmployeesInDateRange("2018-01-01", "2019-11-15");
        assertEquals(2, employeeDataInGivenDateRange.size());
        System.out.println(employeePayrollData);
    }
}
