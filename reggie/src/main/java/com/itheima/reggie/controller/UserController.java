package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.UserDto;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author laijunhan
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request){
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            // 生成验证码随机数
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码：{}", code);
            // 发送验证码，这里省略
            // 保存验证码到session
            request.getSession().setAttribute(phone, code);
            return R.success("验证码发送成功");
        }else{
            return R.error("短信发送失败");
        }
    }

    /**
     * 用户登录
     * 因为此次登录的时候点击获取验证码按钮并没有请求到后台，所以忽略后台保存session的操作
     * @param userDto 用户信息
     * @param request 请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto, HttpServletRequest request){
        String code = userDto.getCode();
        String phone = userDto.getPhone();
        request.getSession().setAttribute(phone, code);
        // 验证验证码
        String code2 = (String)request.getSession().getAttribute(phone);
        if(code2.equals(code)){
            //查询是否在用户表中 如果没有就注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                // 注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            // 验证码正确，登录成功
            request.getSession().setAttribute("user", user.getId());
            return R.success(user);
        }else{
            return R.error("验证码错误");
        }
    }
}
