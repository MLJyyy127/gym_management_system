package com.gym.gymmanagementsystem.service.impl;

import com.gym.gymmanagementsystem.mapper.EquipmentMapper;
import com.gym.gymmanagementsystem.pojo.Equipment;
import com.gym.gymmanagementsystem.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public List<Equipment> findAll() {
        return equipmentMapper.findAll();
    }

    @Override
    public Boolean deleteByEquipmentId(Integer equipmentId) {
        return equipmentMapper.deleteByEquipmentId(equipmentId);
    }

    @Override
    public Boolean insertEquipment(Equipment equipment) {
        return equipmentMapper.insertEquipment(equipment);
    }

    @Override
    public Boolean updateEquipmentByEquipmentId(Equipment equipment) {
        return equipmentMapper.updateEquipmentByEquipmentId(equipment);
    }

    @Override
    public List<Equipment> selectByEquipmentId(Integer equipmentId) {
        return equipmentMapper.selectByEquipmentId(equipmentId);
    }

    @Override
    public Integer selectTotalCount() {
        return equipmentMapper.selectTotalCount();
    }
}
