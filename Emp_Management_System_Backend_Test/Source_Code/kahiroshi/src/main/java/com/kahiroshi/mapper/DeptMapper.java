package com.kahiroshi.mapper;

import com.kahiroshi.object.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeptMapper {

    @Select("select id, name, create_time, last_update_time from dept")
    List<Dept> queryDeptByName();

    @Delete("delete from dept where id = #{id}")
    void deleteDeptById(Integer id);

    //Can omit dept. because only one parameter
    @Insert("insert into dept(name, create_time, last_update_time) values (#{name}, #{createTime}, #{lastUpdateTime})")
    void insertDept(Dept dept);
}
