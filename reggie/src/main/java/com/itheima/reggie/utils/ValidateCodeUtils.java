package com.itheima.reggie.utils;

import java.util.Random;

/**
 * 随机生成验证码工具类
 * @author laijunhan
 */
public class ValidateCodeUtils {
    /**
     * 随机生成验证码
     * @param length 长度为4位或者6位
     * @return 验证码
     */
    public static Integer generateValidateCode(int length){
        Integer code =null;
        if(length == 4){
            code = new Random().nextInt(9999);
            if(code < 1000){
                code = code + 1000;
            }
        }else if(length == 6){
            code = new Random().nextInt(999999);
            if(code < 100000){
                code = code + 100000;
            }
        }else{
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return code;
    }

    /**
     * 随机生成指定长度字符串验证码
     * @param length 长度
     * @return 字符串验证码
     */
    public static String generateValidateCode4String(int length){
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        return hash1.substring(0, length);
    }
}
