package com.kahiroshi.mapper;

import com.kahiroshi.object.Emp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmpMapper {

    @Insert("insert into emp(name, phone, dept_id, create_time, last_update_time)" +
            "values(#{name}, #{phone}, #{deptId}, #{createTime}, #{lastUpdateTime})")
    void insertEmpData(Emp emp);

    //No need @Select tag, use xml to build dynamic SQL
    void deleteEmpByIds(List<Integer> ids);

    List<Emp> queryEmpData(String name, String phone, Integer deptId);

    @Select("select id, name, phone, dept_id, create_time, last_update_time from emp where id = #{id}")
    Emp queryEmpById(Integer id);

    @Update("update emp set " +
            "name = #{name}, phone = #{phone}, dept_id = #{deptId}, last_update_time = #{lastUpdateTime} " +
            "where id = #{id}")
    void updateEmp(Emp emp);
}
