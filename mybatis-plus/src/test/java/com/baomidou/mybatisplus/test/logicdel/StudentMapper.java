package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentMapper extends BaseMapper<Student> {

    @Select("select * from student where id = #{id}")
    Student byId(Long id);

    int testDeleteBatch(@Param(Constants.COLL) List<Student> entityList);

    @Select("select * from student")
    List<Student> selectAllStudent();

}
