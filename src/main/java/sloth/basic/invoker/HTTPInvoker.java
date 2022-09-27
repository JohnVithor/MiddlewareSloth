package sloth.basic.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import sloth.basic.util.MethodObjPair;
import sloth.basic.annotations.*;
import sloth.basic.error.NotFoundException;
import sloth.basic.error.RemotingException;
import sloth.basic.http.HTTPRequest;
import sloth.basic.http.HTTPResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HTTPInvoker implements Invoker<HTTPRequest, HTTPResponse>{

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeParser parser = TypeParser.newBuilder().build();
    private final ConcurrentHashMap<String, MethodObjPair> gets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MethodObjPair> posts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MethodObjPair> puts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MethodObjPair> deletes = new ConcurrentHashMap<>();

    @Override
    public HTTPResponse invoke(HTTPRequest request) throws RemotingException {
        try {
            ConcurrentHashMap<String, MethodObjPair> routes = switch (request.getMethod()) {
                case "GET" -> gets;
                case "POST" -> posts;
                case "PUT" -> puts;
                case "DELETE" -> deletes;
                // case OPTIONS -> Como fazer esse?
                default -> throw new RemotingException("Method: " + request.getMethod() + " not supported");
            };
            return execute(findMethod(routes, request.getQuery(), request.getMethod()), request);
        } catch (NotFoundException e) {
            return new HTTPResponse("HTTP/1.1",404, "Not Found",
                    // TODO: fazer um html de fato
                    HTTPResponse.buildBasicHeaders(e.getMessage()), e.getMessage());
        }
    }

    private MethodObjPair findMethod(ConcurrentHashMap<String, MethodObjPair> target, String query, String verb) throws RemotingException {
        if (target.containsKey(query)) {
            return target.get(query);
        } else {
            throw new NotFoundException("The path " + query + " was not found on the server for the verb " + verb);
        }
    }

    public HTTPResponse execute(MethodObjPair pair, HTTPRequest request) throws RemotingException {
        try {
            List<Object> params = new ArrayList<>();
            for (Parameter p : pair.method().getParameters()) {
                if (p.isAnnotationPresent(Param.class)) {
                    String name = p.getAnnotation(Param.class).name();
                    String value = request.getQueryParams().get(name);
                    if (value == null) {
                        throw new RemotingException("parameter " + name + " not specified");
                    }
                    params.add(parser.parse(value, p.getType()));
                } else {
                    throw new RemotingException("parameter " + p.getName() + "not annotated");
                }
            }
            String response = String.valueOf(pair.method().invoke(pair.obj(), params.toArray()));
            return new HTTPResponse("HTTP/1.1",200, "OK",
                    HTTPResponse.buildBasicHeaders(response), response);
        } catch (IllegalAccessException e) {
            throw new RemotingException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RemotingException(e.getMessage());
        } catch (TypeParserException e) {
            throw new RemotingException(e.getMessage());
        }
    }

    @Override
    public void register(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Get.class)) {
                    method.setAccessible(true);
                    gets.put("/"+clazz.getAnnotation(RequestMapping.class).router()
                            + "/" + method.getAnnotation(Get.class).router()
                    , new MethodObjPair(method, object));
                } else if (method.isAnnotationPresent(Post.class)) {
                    method.setAccessible(true);
                    posts.put("/"+clazz.getAnnotation(RequestMapping.class).router()
                            + "/" + method.getAnnotation(Post.class).router()
                            , new MethodObjPair(method, object));
                } else if (method.isAnnotationPresent(Put.class)) {
                    method.setAccessible(true);
                    puts.put("/"+clazz.getAnnotation(RequestMapping.class).router()
                            + "/" + method.getAnnotation(Put.class).router()
                            , new MethodObjPair(method, object));
                } else if (method.isAnnotationPresent(Delete.class)) {
                    method.setAccessible(true);
                    deletes.put("/"+clazz.getAnnotation(RequestMapping.class).router()
                              + "/" + method.getAnnotation(Delete.class).router()
                            , new MethodObjPair(method, object));
                }
            }
        }
    }
}
