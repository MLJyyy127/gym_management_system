package com.gym.gymmanagementsystem.mapper;

import com.gym.gymmanagementsystem.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

//    方法,根据用户名和密码来查找搜索，泛灰类型时Admin实例类
    Admin selectByAccountAndPassword(Admin admin);
}
