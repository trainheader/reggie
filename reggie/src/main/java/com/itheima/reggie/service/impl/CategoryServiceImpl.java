package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

/**
 * @author laijunhan
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    private DishService dishService;
    private SetmealService setmealService;

    public CategoryServiceImpl(DishService dishService, SetmealService setmealService) {
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    @Override
    public void remove(Long ids) {
        // 构建条件查询语句
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        // 查询数据
        long dishCount = dishService.count(dishLambdaQueryWrapper);
        long setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        // 如果有关联则不能删除且抛出异常
        if (dishCount > 0) {
            throw new CustomException("该分类下存在菜品，不能删除");
        }
        if (setmealCount > 0) {
            throw new CustomException("该分类下存在套餐，不能删除");
        }
        // 删除分类
        this.removeById(ids);
    }
}
