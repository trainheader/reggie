package com.itheima.reggie.common;

/**
 * @author laijunhan
 * 这是一个基于ThreadLocal的上下文对象，用于存储一些全局变量，比如当前登录的用户信息等。
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程要保存一个员工的id
     * @param id 员工id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取当前线程保存的员工id
     * @return 员工id
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
