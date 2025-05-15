package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author laijunhan
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    private final SetmealService setmealService;
    private final CategoryService categoryService;
    private final SetmealDishService setmealDishService;

    public SetmealController(SetmealService setmealService, CategoryService categoryService, SetmealDishService setmealDishService) {
        this.setmealService = setmealService;
        this.categoryService = categoryService;
        this.setmealDishService = setmealDishService;
    }

    /**
     * 保存套餐
     * @param setmealDto 套餐信息
     * @return 保存结果
     */
    @PostMapping("")
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐
     * @param page 页码
     * @param pageSize 一页大小
     * @param name 套餐名称
     * @return 分页结果
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> pageDtoInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, wrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> recordsDto = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            BeanUtils.copyProperties(item, setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());
        pageDtoInfo.setRecords(recordsDto);
        return R.success(pageDtoInfo);
    }

    /**
     * 批量更新套餐状态 0-停售 1-起售
     * @param ids 套餐id列表
     * @return 更新结果
     */
    @PostMapping("/status/0")
    public R<String> changeStatus1(String ids){
        String[] idArr = ids.split(",");
        Long[] idsLong = new Long[idArr.length];
        for (int i = 0; i < idArr.length; i++) {
            idsLong[i] = Long.parseLong(idArr[i]);
        }
        for (Long l : idsLong) {
            Setmeal setmeal = setmealService.getById(l);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }
        return R.success("批量停售成功");
    }

    /**
     * 批量更新套餐状态 0-停售 1-起售
     * @param ids 套餐id列表
     * @return 更新结果
     */
    @PostMapping("/status/1")
    @Transactional
    public R<String> changeStatus2(String ids){
        String[] idArr = ids.split(",");
        Long[] idsLong = new Long[idArr.length];
        for (int i = 0; i < idArr.length; i++) {
            idsLong[i] = Long.parseLong(idArr[i]);
        }
        for (Long l : idsLong) {
            Setmeal setmeal = setmealService.getById(l);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }
        return R.success("批量启售成功");
    }

    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     * @return 删除结果
     */
    @DeleteMapping("")
    @Transactional
    public R<String> delete(String ids){
        String[] idArr = ids.split(",");
        Long[] idsLong = new Long[idArr.length];
        for (int i = 0; i < idArr.length; i++) {
            idsLong[i] = Long.parseLong(idArr[i]);
        }
        // 先删除套餐
        for (Long l : idsLong) {
            Setmeal setmeal = setmealService.getById(l);
            if(setmeal.getStatus() == 1){
                return R.error("启用的套餐不能删除");
            }
            setmealService.removeById(l);
        }
        // 再删除setmeal-dish关联关系
        for (Long l : idsLong) {
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId, l);
            setmealDishService.remove(wrapper);
        }
        return R.success("批量删除成功");
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId 分类id
     * @return 套餐列表
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealByCategoryId(@RequestParam Long categoryId){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId, categoryId);
        List<Setmeal> setmealList = setmealService.list(wrapper);
        return R.success(setmealList);
    }
}
