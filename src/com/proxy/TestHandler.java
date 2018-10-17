package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态搭理AOP实际操作类型
 * Created by muweiliang on 2018/10/16.
 */
public class TestHandler implements InvocationHandler {

    private Object object;

    public TestHandler() {

    }
    public TestHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("开启事物111");
        method.invoke(object,args);
        System.out.println("关闭事物222");
        return null;
    }


}
