package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author laijunhan
 * 菜品服务实现类
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    private final DishFlavorService dishFlavorService;

    public DishServiceImpl(DishFlavorService dishFlavorService) {
        this.dishFlavorService = dishFlavorService;
    }
    /**
     * 保存菜品及其口味信息
     * @param dishDto 菜品及其配料信息
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        dishDto.getFlavors().forEach(flavor -> {
            flavor.setDishId(id);
        });
        // 保存菜品口味数据到数据库
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据ID查询菜品信息和对应的口味信息
     * @param id 菜品ID
     * @return 菜品及其口味息
     */
    @Override
    public DishDto getByIdWithFlavors(Long id) {
        // 从dish查询菜品信息
        Dish dish = this.getById(id);
        // 口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        // 封装返回值
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    /**
     * 更新菜品及其口味信息
     * @param dishDto 菜品及其配料信息
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);
        // 清理当前菜品对应的所有口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加当前的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor -> {
            flavor.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据ID删除菜品及其口味信息
     * @param ids 菜品ID列表
     */
    @Override
    @Transactional
    public void removeByIdWithFlavors(Long ids) {
        // 删除dish表信息
        this.removeById(ids);
        // 删除dish_flavor表信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper);
    }
}
