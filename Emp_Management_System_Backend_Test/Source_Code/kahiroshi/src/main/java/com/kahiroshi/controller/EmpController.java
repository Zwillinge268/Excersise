package com.kahiroshi.controller;

import com.kahiroshi.anno.Log;
import com.kahiroshi.object.Emp;
import com.kahiroshi.object.PageBean;
import com.kahiroshi.object.Result;
import com.kahiroshi.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    /**
     * Insert emp
     * */
    @Log
    @PostMapping
    public Result insertEmp(@RequestBody Emp emp) {
        empService.insertEmp(emp);
        return Result.success();
    }

    /**
     * Delete emp by id(s)
     * */
    @Log
    @DeleteMapping("/{ids}")
    public Result deleteEmp(@PathVariable List<Integer> ids) {
        empService.deleteEmp(ids);
        return Result.success();
    }

    /**
     * Query emp data (List<emp> + Count Total -> Page -> Result.data)
     * @return PageBean
     */
    @GetMapping
    //Date Format: @DataTimeFormat(pattern="yyyy-MM-dd") LocalDate date
    public Result queryEmp(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           String name, String phone, Integer deptId) {
        PageBean pageBeanData = empService.queryEmp(page, pageSize, name, phone, deptId);
        return Result.success(pageBeanData);
    }

    /**
     * Query emp data by id
     * @return emp
     * */
    @GetMapping("/{id}")
    public Result queryEmpById(@PathVariable Integer id) {
        Emp emp = empService.queryEmpById(id);
        return Result.success(emp);
    }

    /**
     * Update emp data
     * */
    @Log
    @PutMapping
    public Result updateEmp(@RequestBody Emp emp) {
        empService.updateEmp(emp);
        return Result.success();
    }
}