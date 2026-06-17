package com.gym.gymmanagementsystem.controller;

import com.gym.gymmanagementsystem.pojo.Admin;
import com.gym.gymmanagementsystem.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiLoginController {

    private  final AdminService adminService;

    public ApiLoginController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/adminLogin")
    public ResponseEntity<Map<String, Object>> adminLogin(Admin admin, HttpSession  session){
        Admin loggedIn = adminService.adminlogin(admin);
        if(loggedIn == null){
            return unauthorized("账号或密码有误");
        }

        return ResponseEntity.ok(singleSuccess());
    }

    // 返回成功的信息
    private  static  Map<String, Object> singleSuccess(){
        Map<String, Object> m = new HashMap<>(2);
        m.put("success",true);
        return m;
    }

    // 返回失败的信息
    private static ResponseEntity<Map<String, Object>> unauthorized(String  message){
        Map<String, Object> m = new HashMap<>(4);
        m.put("success",false);
        m.put("message",message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
    }
}
