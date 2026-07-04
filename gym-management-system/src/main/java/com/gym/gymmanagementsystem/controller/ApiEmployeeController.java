package com.gym.gymmanagementsystem.controller;

import com.gym.gymmanagementsystem.pojo.Employee;
import com.gym.gymmanagementsystem.service.EmployeeService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/employee")
public class ApiEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/selEmployee")
    public Map<String, Object> selectEmployee(){
        List<Employee> employeeList = employeeService.findAll();
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("success",true);
        resp.put("employeeList",employeeList);

        return resp;
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<Map<String, Object>> addEmployee(Employee employee){

        // 生成员工账号
        Random random = new Random();
        String account1 = "1010";

        for (int i = 0; i < 5; i++){
            account1 += random.nextInt(10);
        }
        int account = Integer.parseInt(account1);

        // 日期
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = simpleDateFormat.format(date);

        employee.setEmployeeAccount(account);
        employee.setEntryTime(nowDay);

        employeeService.insertEmployee(employee);

        HashMap<String, Object> resp = new HashMap<>();
        resp.put("success",true);
        return ResponseEntity.ok(resp);


    }

    @PostMapping("/delEmployee")
    public ResponseEntity<Map<String, Object>> delEmployee(Integer employeeAccount){
        employeeService.deleteByEmployeeAccount(employeeAccount);
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("success",true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/updateEmployee")
    public ResponseEntity<Map<String, Object>> updateEmployee(Employee employee){
        employeeService.updateMemberByEmployeeAccount(employee);
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("success",true);
        return ResponseEntity.ok(resp);
    }

    // 编辑员工信息：跳转页面
    @GetMapping("/toUpdateEmployee")
    public Map<String, Object> toUpdateEmployee(Integer employeeAccount){
        List<Employee> employeeList = employeeService.selectByEmployeeAccount(employeeAccount);
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("success",true);
        resp.put("employeeList",employeeList);
        return resp;
    }
}
