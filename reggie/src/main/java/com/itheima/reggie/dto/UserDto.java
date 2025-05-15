package com.itheima.reggie.dto;

import com.itheima.reggie.entity.User;
import lombok.Data;

/**
 * @author laijunhan
 */
@Data
public class UserDto extends User {
    String code;
}
