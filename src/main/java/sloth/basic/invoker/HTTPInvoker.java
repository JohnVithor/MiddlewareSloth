package sloth.basic.invoker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import sloth.basic.error.BadRequestException;
import sloth.basic.error.HTTPErrorResponseBuilder;
import sloth.basic.util.RouteInfo;
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
    private final ConcurrentHashMap<String, List<RouteInfo>> routes = new ConcurrentHashMap<>();

    @Override
    public HTTPResponse invoke(HTTPRequest request) throws RemotingException {
        List<RouteInfo> methods = routes.get(request.getQuery());
        if (methods == null || methods.isEmpty()) {
            throw new NotFoundException("Route : " + request.getQuery() + " not found");
        }
        Optional<RouteInfo> found = methods
            .stream()
            .filter(routeInfo -> routeInfo.verb().equals(request.getMethod()))
            .findFirst();
        if (found.isEmpty()) {
            throw new BadRequestException("Method: " + request.getMethod() + " not supported on " + request.getQuery());
        }
        return execute(found.get(), request);
    }

    public HTTPResponse execute(RouteInfo info, HTTPRequest request) throws RemotingException {
        try {
            List<Object> params = new ArrayList<>();
            for (Parameter p : info.method().getParameters()) {
                if (p.isAnnotationPresent(Param.class)) {
                    String name = p.getAnnotation(Param.class).name();
                    String value = request.getQueryParams().get(name);
                    if (value == null) {
                        throw new RemotingException("parameter " + name + " not specified");
                    }
                    params.add(parser.parse(value, p.getType()));
                } else if (p.isAnnotationPresent(Body.class)) {
                    try {
                        params.add(mapper.readValue(request.getBody(), p.getType()));
                    } catch (Exception e) {
                        params.add(parser.parse(request.getBody(), p.getType()));
                    }
                } else {
                    throw new RemotingException("parameter " + p.getName() + "not annotated");
                }
            }
            String response = mapper.writeValueAsString(info.method().invoke(info.obj(), params.toArray()));
            return new HTTPResponse("HTTP/1.1",200, "OK",
                    HTTPResponse.buildBasicHeaders(response, info.content_type()), response);
        } catch (IllegalAccessException e) {
            throw new RemotingException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RemotingException(e.getMessage());
        } catch (TypeParserException e) {
            throw new BadRequestException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void register(Object object) {
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MethodMapping.class)) {
                    MethodMapping annot = method.getAnnotation(MethodMapping.class);
                    method.setAccessible(true);
                    String route;
                    if (annot.path().equals("")) {
                        route = "/"+clazz.getAnnotation(RequestMapping.class).path();
                    } else {
                        route = "/"+clazz.getAnnotation(RequestMapping.class).path() + "/" + annot.path();
                    }
                    List<RouteInfo> methods = routes.getOrDefault(route, new ArrayList<>());
                    methods.add(new RouteInfo(annot.method(), annot.content_type(), method, object));
                    routes.put(route, methods);
                }
            }
        }
    }
}
