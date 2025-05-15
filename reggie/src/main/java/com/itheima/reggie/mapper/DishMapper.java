package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author laijunhan
 * 菜品Mapper
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
