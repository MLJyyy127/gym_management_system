package com.gym.gymmanagementsystem.service.impl;

import com.gym.gymmanagementsystem.mapper.AdminMapper;
import com.gym.gymmanagementsystem.pojo.Admin;
import com.gym.gymmanagementsystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin adminlogin(Admin admin) {
        return adminMapper.selectByAccountAndPassword(admin);
    }
}
