package proxy;

import java.lang.reflect.Proxy;

/**
 * 测试AOP动态搭理
 * Created by muweiliang on 2018/10/16.
 */
public class TestAOP {

    public static void main(String[] args) throws Exception {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
       Student student = new Student();
        Person person =
                (Person) Proxy.newProxyInstance(Student.class.getClassLoader(),Student.class.getInterfaces(),new TestHandler(student));
        System.out.println(person.getClass().getName());
        person.sayHello();

    }

}
