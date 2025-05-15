package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author laijunhan
 * 菜品控制器
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    private final DishService dishService;
    private final CategoryService categoryService;
    private final DishFlavorService dishFlavorService;

    public DishController(DishService dishService, CategoryService categoryService, DishFlavorService dishFlavorService) {
        this.dishService = dishService;
        this.categoryService = categoryService;
        this.dishFlavorService = dishFlavorService;
    }

    /**
     * 保存菜品信息
     * @param dishDto 菜品信息
     * @return 保存结果
     */
    @PostMapping("")
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功");
    }

    /**
     * 分页查询菜品列表
     * @param page 页码
     * @param pageSize 每页条数
     * @param name 菜品名称
     * @return 分页数据
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageInfoDto = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<Dish>()
                .like(name != null, Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, wrapper);
        // 拷贝数据到Dto
        BeanUtils.copyProperties(pageInfo, pageInfoDto, "records");
        List<Dish> pageRecords = pageInfo.getRecords();

        List<DishDto> dishDtoRecords = pageRecords.stream().map(dish -> {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(dish, dto);
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dto.setCategoryName(categoryName);
            return dto;
        }).collect(Collectors.toList());

        pageInfoDto.setRecords(dishDtoRecords);

        return R.success(pageInfoDto);
    }

    /**
     * 根据ID查询菜品信息
     * @param id 菜品ID
     * @return 菜品信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto byIdWithFlavors = dishService.getByIdWithFlavors(id);
        return R.success(byIdWithFlavors);
    }

    /**
     * 修改菜品信息
     * @param dishDto 菜品信息
     * @return 保存结果
     */
    @PutMapping("")
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

    /**
     * 删除菜品信息
     * @param ids 菜品ID列表
     * @return 删除结果
     */
    @DeleteMapping("")
    @Transactional
    public R<String> delete(@RequestParam("ids") String ids){
        String[] split = ids.split(",");
        Long[] newIds = new Long[split.length];
        for (int i = 0; i < split.length; i++) {
            newIds[i] = Long.parseLong(split[i]);
        }
        for (Long newId : newIds) {
            // 若菜品在售，则不能删除
            Dish dish = dishService.getById(newId);
            if (dish.getStatus() == 1) {
                return R.error("菜品在售，不能删除");
            }
            dishService.removeByIdWithFlavors(newId);
        }
        return R.success("删除成功");
    }

    /**
     * 修改菜品状态 起售 改为 停售
     * @param ids 菜品ID列表
     * @return 修改结果
     */
    @PostMapping("/status/0")
    public R<String> updateStatus1(@RequestParam String ids){
        String[] split = ids.split(",");
        Long[] newIds = new Long[split.length];
        for (int i = 0; i < split.length; i++) {
            newIds[i] = Long.parseLong(split[i]);
        }
        for (Long newId : newIds) {
            Dish dish = dishService.getById(newId);
            dish.setStatus(0);
            dishService.updateById(dish);
        }
        return R.success("修改状态成功");
    }

    /**
     * 修改菜品状态 停售 改为 起售
     * @param ids 菜品ID列表
     * @return 修改结果
     */
    @PostMapping("/status/1")
    public R<String> updateStatus2(@RequestParam String ids){
        String[] split = ids.split(",");
        Long[] newIds = new Long[split.length];
        for (int i = 0; i < split.length; i++) {
            newIds[i] = Long.parseLong(split[i]);
        }
        for (Long newId : newIds) {
            Dish dish = dishService.getById(newId);
            dish.setStatus(1);
            dishService.updateById(dish);
        }
        return R.success("修改状态成功");
    }

//    /**
//     * 根据菜品分类ID查询菜品列表
//     * @param categoryId 分类ID
//     * @return 菜品列表
//     */
//    @GetMapping(value = "/list", params = "categoryId")
//    public R<List<Dish>> getDishByCategoryId(@RequestParam("categoryId") Long categoryId){
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//
//        wrapper.eq(Dish::getCategoryId, categoryId);
//        wrapper.eq(Dish::getStatus, 1);
//        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(wrapper);
//        return R.success(list);
//    }

    /**
     * 根据菜品分类名称查询菜品列表
     * @param name 分类名称
     * @return 菜品列表
     */
    @GetMapping(value = "/list", params = "name")
    public R<List<Dish>> getDishByCategoryName(@RequestParam("name") String name){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        Long id = categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getName, name)).getId();

        wrapper.eq(Dish::getCategoryId, id);
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }

    /**
     * 根据分类信息查询菜品列表包括其口味信息
     * @param dish 菜品分类封装的信息
     * @return 菜品列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDish(Dish dish){
        List<DishDto> dishDto;
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        List<Dish> list = dishService.list(wrapper);
        dishDto = list.stream().map(item -> {
           DishDto dto = new DishDto();
           BeanUtils.copyProperties(item, dto);
           LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
           wrapper1.eq(DishFlavor::getDishId, item.getId());
           // 查询口味信息
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper1);
            dto.setFlavors(dishFlavorList);
            return dto;
        }).collect(Collectors.toList());
        return R.success(dishDto);
    }
}
