package sloth.basic.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import sloth.basic.annotations.route.Body;
import sloth.basic.annotations.route.MethodMapping;
import sloth.basic.annotations.route.Param;
import sloth.basic.annotations.route.RequestMapping;
import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.error.RemotingException;
import sloth.basic.http.error.BadRequestException;
import sloth.basic.http.error.InternalServerErrorException;
import sloth.basic.http.error.MethodNotAllowedException;
import sloth.basic.http.error.NotFoundException;
import sloth.basic.http.util.RouteInfos;
import sloth.basic.invoker.InvocationInterceptor;
import sloth.basic.invoker.Invoker;
import sloth.basic.http.util.Route;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class HTTPInvoker implements Invoker<HTTPRequest, HTTPResponse> {

    private final TreeSet<InvocationInterceptor<HTTPRequest, HTTPResponse>> hooks = new TreeSet<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeParser parser = TypeParser.newBuilder().build();
    private final ConcurrentHashMap<String, RouteInfos> routes = new ConcurrentHashMap<>();

    @Override
    public Stream<InvocationInterceptor<HTTPRequest, HTTPResponse>> getHooks() {
        return hooks.stream();
    }

    @Override
    public HTTPResponse invoke(HTTPRequest request) throws RemotingException {
        RouteInfos methods = routes.get(request.getQuery());
        if (methods == null) {
            throw new NotFoundException("Route : " + request.getQuery() + " not found");
        }
        Optional<Route> found = methods.get(request.getMethod());
        if (found.isEmpty()) {
            throw new MethodNotAllowedException("Method: " + request.getMethod() + " not supported on " + request.getQuery());
        }
        return execute(found.get(), request);
    }

    public HTTPResponse execute(Route info, HTTPRequest request) throws RemotingException {
        try {
            List<Object> params = new ArrayList<>();
            for (Parameter p : info.method().getParameters()) {
                if (p.isAnnotationPresent(Param.class)) {
                    String name = p.getAnnotation(Param.class).name();
                    String value = request.getQueryParams().get(name);
                    if (value == null) {
                        throw new BadRequestException("Parameter " + name + " not specified");
                    }
                    params.add(parser.parse(value, p.getType()));
                } else if (p.isAnnotationPresent(Body.class)) {
                    // Antes usava try-catch, e não tinha esse if do content-type
                    if (info.content_type().contains("application/json")) {
                        params.add(mapper.readValue(request.getBody(), p.getType()));
                    } else {
                        params.add(parser.parse(request.getBody(), p.getType()));
                    }
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
    public void registerRoutes(Object object) throws MiddlewareConfigurationException {
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
                    for (Parameter p : method.getParameters()) {
                        if (!(p.isAnnotationPresent(Param.class) || p.isAnnotationPresent(Body.class))) {
                            throw new MiddlewareConfigurationException("Parameter " + p.getName() + " not annotated with" +
                                    " 'Param' or 'Body'" + " on method '"+method.getName()+"' of route " + route);
                        }
                    }
                    RouteInfos methods = routes.getOrDefault(route, new RouteInfos());
                    try {
                        methods.add(new Route(annot.method(), annot.content_type(), method, object));
                    } catch (MiddlewareConfigurationException e) {
                        throw new MiddlewareConfigurationException(e.getMessage() + " on route: " + route);
                    }
                    routes.put(route, methods);
                }
            }
        }
    }

    @Override
    public void registerConf(InvocationInterceptor<HTTPRequest, HTTPResponse> conf) {
        hooks.add(conf);
    }
}
