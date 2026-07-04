package com.gym.gymmanagementsystem.service;

import com.gym.gymmanagementsystem.pojo.Employee;

import java.util.List;

public interface EmployeeService {
    Integer selectTotalCount();
    List<Employee> findAll();
    Boolean insertEmployee(Employee employee);
    Boolean deleteByEmployeeAccount(Integer employeeAccount);
    Boolean updateMemberByEmployeeAccount(Employee employee);
    List<Employee> selectByEmployeeAccount(Integer employeeAccount);
}
