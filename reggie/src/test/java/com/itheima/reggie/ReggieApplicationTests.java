package com.itheima.reggie;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ReggieApplicationTests {

	@Autowired
	EmployeeMapper mapper;

	@Test
	void contextLoads() {
		LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
		wrapper.like(Employee::getName, "朱");

		IPage<Employee> page = new Page<>(1, 3);
		IPage<Employee> employeeIPage = mapper.selectPage(page, wrapper);
		List<Employee> result = employeeIPage.getRecords();
		for (Employee employee : result) {
			System.out.println(employee);
		}
	}

}
