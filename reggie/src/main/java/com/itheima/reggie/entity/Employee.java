package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 * @author laijunhan
 * TableField 注解的 fill 属性，用于控制该字段在 insert 和 update 时是否填充，
 * 默认值为 FieldFill.DEFAULT，即不填充。
 * 类名 驼峰转下划线，即 Employee -> employee 为表名
 * 字段名 驼峰转下划线，即 createTime -> create_time 为列名
 * 名id字段为主键
 * TableName 注解的 value 属性，用于指定表名，默认值为类名的驼峰转下划线形式。
 * TableField 用来指定表中的普通字段信息，用在is开头的字段上，还有变量名和数据库关键字冲突的情况。
 * TableField(fill = FieldFill.INSERT) 注解的作用：
 *     1、在 insert 时，该字段的值将被自动填充
 *     2、在 update 时，该字段的值将被忽略
 * TableField(fill = FieldFill.INSERT_UPDATE) 注解的作用：
 *     1、在 insert 时，该字段的值将被自动填充
 *     2、在 update 时，该字段的值将被更新
 * TableField(exist = false) 注解的作用：
 * 当属性不存在于数据库中时，exist属性设置为false，表示该字段不存在于数据库中。
 */
@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
