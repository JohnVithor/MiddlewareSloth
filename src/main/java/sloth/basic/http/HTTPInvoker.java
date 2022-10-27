package sloth.basic.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import sloth.basic.invoker.Invoker;
import sloth.basic.http.util.Route;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HTTPInvoker extends Invoker<HTTPRequest, HTTPResponse> {


    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    private final TypeParser parser = TypeParser.newBuilder().build();
    private final ConcurrentHashMap<String, RouteInfos> routes = new ConcurrentHashMap<>();

    @Override
    public HTTPResponse invoke(HTTPRequest request) throws RemotingException {
        RouteInfos methods = routes.get(request.query());
        if (methods == null) {
            throw new NotFoundException("Route : " + request.query() + " not found");
        }
        Optional<Route> found = methods.get(request.method());
        if (found.isEmpty()) {
            throw new MethodNotAllowedException("Method: " + request.method() + " not supported on " + request.query());
        }
        return execute(found.get(), request);
    }

    public HTTPResponse execute(Route info, HTTPRequest request) throws RemotingException {
        try {
            List<Object> params = new ArrayList<>();
            for (Parameter p : info.method().getParameters()) {
                if (p.isAnnotationPresent(Param.class)) {
                    String name = p.getAnnotation(Param.class).name();
                    String value = request.queryParams().get(name);
                    if (value == null) {
                        throw new BadRequestException("Parameter " + name + " not specified");
                    }
                    params.add(parser.parseType(value, p.getParameterizedType()));
                } else if (p.isAnnotationPresent(Body.class)) {
                    // Antes usava try-catch, e não tinha esse if do content-type
                    if (info.content_type().contains("application/json")) {
                        params.add(mapper.readValue(request.body(), p.getType()));
                    } else {
                        params.add(parser.parseType(request.body(), p.getParameterizedType()));
                    }
                }
            }
            String response = mapper.writeValueAsString(info.method().invoke(info.obj(), params.toArray()));
            return new HTTPResponse("HTTP/1.1",200, "OK",
                    HTTPResponse.buildBasicHeaders(response, info.content_type()), response);
        } catch (IllegalAccessException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new InternalServerErrorException(e.getMessage() == null?sw.toString():e.getMessage());
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new InternalServerErrorException(e.getMessage() == null?sw.toString():e.getMessage());
        } catch (TypeParserException | JsonProcessingException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new BadRequestException(e.getMessage() == null?sw.toString():e.getMessage());
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
                    configurationsNotify(route, methods);
                }
            }
        }
    }
}
