package sloth.basic.invoker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import sloth.basic.annotations.route.Body;
import sloth.basic.annotations.route.MethodMapping;
import sloth.basic.annotations.route.Param;
import sloth.basic.annotations.route.RequestMapping;
import sloth.basic.error.BadRequestException;
import sloth.basic.error.InternalServerErrorException;
import sloth.basic.util.RouteInfo;
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

    private final TreeSet<InvocationInterceptor> hooks = new TreeSet<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeParser parser = TypeParser.newBuilder().build();
    private final ConcurrentHashMap<String, List<RouteInfo>> routes = new ConcurrentHashMap<>();

    @Override
    public void beforeInvoke(HTTPRequest request) throws RemotingException {
        for (InvocationInterceptor e: hooks) {
            e.beforeRequest(request);
        }
    }

    @Override
    public void afterInvoke(HTTPRequest request, HTTPResponse response) throws RemotingException {
        for (InvocationInterceptor e: hooks) {
            e.afterResponse(request, response);
        }
    }

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
                        throw new BadRequestException("parameter " + name + " not specified");
                    }
                    params.add(parser.parse(value, p.getType()));
                } else if (p.isAnnotationPresent(Body.class)) {
                    // Antes usava try-catch, e não tinha esse if do content-type
                    if (info.content_type().contains("application/json")) {
                        params.add(mapper.readValue(request.getBody(), p.getType()));
                    } else {
                        params.add(parser.parse(request.getBody(), p.getType()));
                    }
                } else {
                    // TODO: mover caso para o momento de registro do método
                    // temporariamente será assumido que o parametro irá como null
                    params.add(null);
//                    throw new BadRequestException("parameter " + p.getName() + "not annotated");
                }
            }
            String response = mapper.writeValueAsString(info.method().invoke(info.obj(), params.toArray()));
            return new HTTPResponse("HTTP/1.1",200, "OK",
                    HTTPResponse.buildBasicHeaders(response, info.content_type()), response);
        } catch (IllegalAccessException e) {
            // teoricamente não pode acontecer, dado que o acesso ao método foi setado como true
            throw new InternalServerErrorException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (TypeParserException | JsonProcessingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void registerRoutes(Object object) {
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

    @Override
    public void registerConf(InvocationInterceptor conf) {
        hooks.add(conf);
    }
}
