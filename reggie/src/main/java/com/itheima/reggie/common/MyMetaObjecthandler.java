package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author laijunhan
 */
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 插入时，填充默认值
     * ThreadLocal 是线程的变量，其中可以保存一些数据，在同一个线程中可以共享，不同线程之间是隔离的。
     * 这里的 ThreadLocal 主要用来保存当前线程的用户信息，比如当前登录的用户 ID。
     * 当登入的时候，我们可以将用户 ID 保存到 ThreadLocal 中，然后在 insertFill 方法中，将用户 ID 填充到 createUser 和 updateUser 字段中。
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
