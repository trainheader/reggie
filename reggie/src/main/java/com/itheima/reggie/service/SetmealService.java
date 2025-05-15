package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

/**
 * @author laijunhan
 */
public interface SetmealService extends IService<Setmeal>{
    public void saveWithDish(SetmealDto setmealDto);
}
