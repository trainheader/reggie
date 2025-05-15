package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

/**
 * @author laijunhan
 */
public interface DishService extends IService<Dish> {
    // 新增菜品 同时插入菜品对应的口味数据 要操作两张表dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavors(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeByIdWithFlavors(Long ids);
}
