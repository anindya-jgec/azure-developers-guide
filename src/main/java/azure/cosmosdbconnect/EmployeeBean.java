package azure.cosmosdbconnect;

public class EmployeeBean {
    public String employeeName;
    public String employeeDepartment;
    public String employeeAddress;
    public String employeeZip;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeDepartment() {
        return employeeDepartment;
    }

    public void setEmployeeDepartment(String employeeDepartment) {
        this.employeeDepartment = employeeDepartment;
    }

    public String getEmployeeAddress() {
        return employeeAddress;
    }

    public void setEmployeeAddress(String employeeAddress) {
        this.employeeAddress = employeeAddress;
    }

    public String getEmployeeZip() {
        return employeeZip;
    }

    public void setEmployeeZip(String employeeZip) {
        this.employeeZip = employeeZip;
    }
}