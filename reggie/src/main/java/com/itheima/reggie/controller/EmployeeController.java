package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author laijunhan
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 登录
     * @param session session对象
     * @param employee 员工对象3
     * @return 登录结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {
        // 获取密码并加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 通过用户名查询员工
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 判断是否有员工
        if(emp == null){
            return R.error("登录失败");
        }

        // 密码比对
        if(!password.equals(emp.getPassword())){
            return R.error("登录失败");
        }

        // 判断用户账户状态
        if(emp.getStatus() == 0){
            return R.error("登陆失败");
        }

        // 登录成功 将员工id存入session
        session.setAttribute("employeeId", emp.getId());

        return R.success(emp);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("employeeId");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @param employee 员工对象
     * @return 消息对象
     */
    @PostMapping("")
    public R<String> add(@RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("添加员工成功");
    }

    /**
     * 分页查询员工
     * @param page 页码
     * @param pageSize 页大小
     * @param name 搜索查询的员工姓名
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name){
        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);
        // 分页构造器
        Page<Employee> employeePage = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 查询
        employeePage = employeeService.page(employeePage, queryWrapper);
        return R.success(employeePage);
    }

    /**
     * 根据id修改员工
     * @param employee 员工对象
     * @return 消息对象
     */
    @PutMapping("")
    public R<String>  update(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success("修改员工成功");
    }

    /**
     * 根据id查询员工
     * @param id 员工id
     * @return 结果对象
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
