package com.kahiroshi.service.impl;

import com.kahiroshi.mapper.DeptMapper;
import com.kahiroshi.object.Dept;
import com.kahiroshi.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<Dept> queryDeptByName() {
        return deptMapper.queryDeptByName();
    }

    @Override
    public void deleteDeptById(Integer id) {
        deptMapper.deleteDeptById(id);
    }

    @Override
    public void insertDept(Dept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setLastUpdateTime(LocalDateTime.now());
        deptMapper.insertDept(dept);
    }
}
