package com.kahiroshi.service;

import com.kahiroshi.object.Emp;
import com.kahiroshi.object.PageBean;

import java.util.List;

public interface EmpService {
    void insertEmp(Emp emp);

    void deleteEmp(List<Integer> ids);

    PageBean queryEmp(Integer page, Integer pageSize, String name, String phone, Integer deptId);

    Emp queryEmpById(Integer id);

    void updateEmp(Emp emp);
}
