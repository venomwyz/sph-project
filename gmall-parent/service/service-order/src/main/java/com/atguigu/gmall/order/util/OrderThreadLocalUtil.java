package com.atguigu.gmall.order.util;

/**
 * 自定义的本地线程工具类,目的是为了将名字存入本地线程
 */
public class OrderThreadLocalUtil {

    //初始化
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 取值方法
     * @return
     */
  public static String get(){
      return threadLocal.get();
  }

    /**
     * 存储值
     * @param username
     */
    public static void set(String username){
        threadLocal.set(username);
    }
}
