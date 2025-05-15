package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author laijunhan
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final AddressBookService addressBookService;
    private final OrderDetailService orderDetailService;
    public OrderServiceImpl(ShoppingCartService shoppingCartService, UserService userService, AddressBookService addressBookService, OrderDetailService orderDetailService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.addressBookService = addressBookService;
        this.orderDetailService = orderDetailService;
    }
    /**
     * 提交订单
     * @param orders 订单信息
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        Long currentId = BaseContext.getCurrentId();
        orders.setUserId(currentId);

        // 查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        if (list.isEmpty()) {
            throw new CustomException("购物车为空，无法提交订单");
        }

        // 查询用户数据
        User user = userService.getById(currentId);

        // 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        // 向订单表插入数据
        long id = IdWorker.getId();
        orders.setNumber(String.valueOf(id));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setPayMethod(1);
        orders.setRemark("没有remark");
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        orders.setAddress((addressBook.getProvinceName() != null ? addressBook.getProvinceName() : "") +
                        (addressBook.getCityName() != null ? addressBook.getCityName() : "") +
                        (addressBook.getDistrictName() != null ? addressBook.getDistrictName() : "")
                );
        orders.setConsignee(addressBook.getConsignee());



        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetailList= list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setImage(item.getImage());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).toList();

        orders.setAmount(new BigDecimal(amount.get()));
        // 向订单表插入数据
        this.save(orders);

        // 向订单明细表插入数据
        orderDetailService.saveBatch(orderDetailList);

        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
