package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author laijunhan
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    public OrderController(OrderService orderService, OrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    /**
     * 提交订单
     * @param orders 订单信息
     * @return 提交结果
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("订单提交成功");
    }

    /**
     * 获取订单详情
     * @param page 页码
     * @param pageSize 页大小
     * @return 订单详情列表
     */
    @GetMapping("/userPage")
    public R<Page<OrderDetail>> userPage(Integer page, Integer pageSize){
        Page<OrderDetail> orderDetailPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Orders> orderWrapper = new LambdaQueryWrapper<>();

        orderWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        orderWrapper.orderByDesc(Orders::getCheckoutTime);
        List<Orders> list = orderService.list(orderWrapper);
        Orders orders = list.get(0);
        Long ordersId = orders.getId();

        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, ordersId);
        Page<OrderDetail> orderDetailPage1 = orderDetailService.page(orderDetailPage, orderDetailLambdaQueryWrapper);
        return R.success(orderDetailPage1);
    }
}
