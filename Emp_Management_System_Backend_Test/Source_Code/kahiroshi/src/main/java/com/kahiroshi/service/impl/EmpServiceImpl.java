package com.kahiroshi.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kahiroshi.mapper.EmpMapper;
import com.kahiroshi.object.Emp;
import com.kahiroshi.object.PageBean;
import com.kahiroshi.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;

    @Override
    public void insertEmp(Emp emp) {
        emp.setCreateTime(LocalDateTime.now());
        emp.setLastUpdateTime(LocalDateTime.now());
        empMapper.insertEmpData(emp);
    }

    @Override
    public void deleteEmp(List<Integer> ids) {
        empMapper.deleteEmpByIds(ids);
    }

    @Override
    public PageBean queryEmp(Integer page, Integer pageSize, String name, String phone, Integer deptId) {
        PageHelper.startPage(page, pageSize);
        List<Emp> empList = empMapper.queryEmpData(name, phone, deptId);
        Page<Emp> pageList = (Page<Emp>)empList;
        return new PageBean(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public Emp queryEmpById(Integer id) {
        return empMapper.queryEmpById(id);
    }

    @Override
    public void updateEmp(Emp emp) {
        emp.setLastUpdateTime(LocalDateTime.now());
        empMapper.updateEmp(emp);
    }
}