package com.kahiroshi.mapper;

import com.kahiroshi.object.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogMapper {

    @Insert("insert into log(operate_user, operate_time, method_name, method_params, return_value)" +
            "values (#{operateUser}, #{operateTime}, #{methodName}, #{methodParams}, #{returnValue})")
    public void insertLog(OperateLog log);
}
