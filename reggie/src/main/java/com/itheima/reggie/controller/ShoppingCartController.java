package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author laijunhan
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    /**
     * 添加商品到购物车 可以是菜品也可以是套餐
     *
     * @param shoppingCart 商品信息
     * @return 添加成功返回结果类
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {
        // 设置该商品购买人的ID 即userId
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 判断是单独的菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            // 判断是否已经在购物车中，如果有就数量加1，如果没有就新增一个
            Long dishId = shoppingCart.getDishId();
            wrapper.eq(ShoppingCart::getDishId, dishId);
            ShoppingCart one = shoppingCartService.getOne(wrapper);
            if (one != null) {
                // 存在就数量加1
                one.setNumber(one.getNumber() + 1);
                shoppingCartService.updateById(one);
            } else {
                // 不存在就新增一个
                shoppingCart.setNumber(1);
                shoppingCartService.save(shoppingCart);
            }
        } else {
            // 是套餐
            Long setmealId = shoppingCart.getSetmealId();
            wrapper.eq(ShoppingCart::getSetmealId, setmealId);
            ShoppingCart one = shoppingCartService.getOne(wrapper);
            if (one != null) {
                one.setNumber(one.getNumber() + 1);
                shoppingCartService.updateById(one);
            } else {
                shoppingCart.setNumber(1);
                // 不存在就新增一个
                shoppingCartService.save(shoppingCart);
            }
        }
        return R.success("添加成功");
    }

    /**
     * 查看购物车列表
     *
     * @return 购物车列表
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    /**
     * 删除购物车中的商品
     *
     * @param shoppingCart 商品信息
     * @return 删除成功返回结果类
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        wrapper.eq(ShoppingCart::getDishId, dishId);
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        // 是菜品
        if (one != null) {
            if (one.getNumber() == 1) {
                shoppingCartService.remove(wrapper);
            } else {
                one.setNumber(one.getNumber() - 1);
                shoppingCartService.updateById(one);
            }
        } else {
            LambdaQueryWrapper<ShoppingCart> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.eq(ShoppingCart::getUserId, userId);
            wrapper2.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            ShoppingCart one2 = shoppingCartService.getOne(wrapper2);
            if (one2 != null) {
                if (one2.getNumber() == 1) {
                    shoppingCartService.remove(wrapper2);
                } else {
                    one2.setNumber(one2.getNumber() - 1);
                    shoppingCartService.updateById(one2);
                }
            }
        }
        return R.success("删除成功");
    }

    /**
     * 清空购物车
     * @return 清空成功返回结果类
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success("清空购物车成功");
    }
}
