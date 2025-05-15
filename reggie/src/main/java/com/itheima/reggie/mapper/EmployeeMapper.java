package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author laijunhan
 * 继承了mybatis-plus的BaseMapper，并添加了Employee实体类作为泛型参数
 * 这样就可以使用BaseMapper中的方法来操作Employee表
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
