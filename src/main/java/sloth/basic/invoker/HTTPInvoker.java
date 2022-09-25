package sloth.basic.invoker;

import sloth.basic.annotations.Get;
import sloth.basic.annotations.Post;
import sloth.basic.annotations.RequestMapping;
import sloth.basic.http.HTTPRequest;
import sloth.basic.http.HTTPResponse;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class HTTPInvoker implements Invoker<HTTPRequest, HTTPResponse>{

    private final ConcurrentHashMap<String, Method> gets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Method> posts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Method> puts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Method> deletes = new ConcurrentHashMap<>();

    @Override
    public HTTPResponse invoke(HTTPRequest request) {
        return null;
    }

    @Override
    public void register(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Get.class)) {
                    method.setAccessible(true);
                    gets.put(clazz.getAnnotation(RequestMapping.class).router()
                            + "/" + method.getAnnotation(Get.class).router()
                    , method);
                    System.out.println(method.getName());
                    System.out.println(Arrays.toString(method.getParameterTypes()));
                    System.out.println(method.getReturnType());
                    System.out.println(Arrays.deepToString(method.getParameterAnnotations()));
                }
            }
        }
    }
}
