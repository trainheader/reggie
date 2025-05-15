package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * @author laijunhan
 */
public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
