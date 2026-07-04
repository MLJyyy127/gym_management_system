package com.gym.gymmanagementsystem.service.impl;

import com.gym.gymmanagementsystem.mapper.EmployeeMapper;
import com.gym.gymmanagementsystem.pojo.Employee;
import com.gym.gymmanagementsystem.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public Integer selectTotalCount() {
        return employeeMapper.selectTotalCount();
    }

    @Override
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    @Override
    public Boolean insertEmployee(Employee employee) {
        return employeeMapper.insertEmployee(employee);
    }

    @Override
    public Boolean deleteByEmployeeAccount(Integer employeeAccount) {
        return employeeMapper.deleteByEmployeeAccount(employeeAccount);
    }

    @Override
    public Boolean updateMemberByEmployeeAccount(Employee employee) {
        return employeeMapper.updateMemberByEmployeeAccount(employee);
    }

    @Override
    public List<Employee> selectByEmployeeAccount(Integer employeeAccount) {
        return employeeMapper.selectByEmployeeAccount(employeeAccount);
    }
}
