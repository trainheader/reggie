package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐 - 菜品关联表 Mapper
 * @author laijunhan
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
