package com.kahiroshi.service;

import com.kahiroshi.object.Dept;

import java.util.List;

public interface DeptService {

    List<Dept> queryDeptByName();

    void deleteDeptById(Integer id);

    void insertDept(Dept dept);
}
