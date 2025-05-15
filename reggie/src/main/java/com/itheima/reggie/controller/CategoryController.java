package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author laijunhan
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    public CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    /**
     * 保存菜品分类信息
     * @param category 菜品分类信息
     * @return 保存结果
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        service.save(category);
        return R.success("菜品分类保存成功");
    }

    /**
     * 分页查询
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页数据
     */
    @GetMapping("/page")
    public R<Page<Category>> page(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "pageSize") Integer pageSize){
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        service.page(categoryPage, wrapper);
        return R.success(categoryPage);
    }

    /**
     * 根据ID删除菜品分类信息
     * @param ids id 列表
     * @return 删除结果
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        service.remove(ids);
        return R.success("菜品分类删除成功");
    }

    /**
     * 根据ID修改菜品分类信息
     * @param category 菜品分类信息
     * @return 修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        service.updateById(category);
        return R.success("菜品分类更新成功");
    }

    /**
     * 获取菜品的分类列表 或者是 套餐的分类列表
     * @param category 分类信息 在其中拿到分类类型 type 通过此判断是获取菜品分类还是套餐分类
     * @return 分类列表
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        // 条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 查询数据
        List<Category> list = service.list(wrapper);
        return R.success(list);
    }
}
