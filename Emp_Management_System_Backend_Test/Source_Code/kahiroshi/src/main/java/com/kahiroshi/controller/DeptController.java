package com.kahiroshi.controller;

import com.kahiroshi.anno.Log;
import com.kahiroshi.object.Dept;
import com.kahiroshi.object.Result;
import com.kahiroshi.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * Query department by department name
     * */
    @GetMapping
    public Result queryDeptByName() {
        List<Dept> deptList = deptService.queryDeptByName();
        return Result.success(deptList);
    }

    //Get data from url -> add @PathVariable
    //Get data from body -> add @RequestBody

    /**
     * Delete department by department id
     * */
    @Log
    @DeleteMapping("/{id}")
    public Result deleteDeptById(@PathVariable Integer id) {
        deptService.deleteDeptById(id);
        return Result.success();
    }

    /**
     * Insert department
     * */
    @Log
    @PostMapping
    public Result insertDept(@RequestBody Dept dept) {
        deptService.insertDept(dept);
        return Result.success();
    }
}
