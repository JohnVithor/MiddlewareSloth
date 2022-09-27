package sloth.basic.util;

import sloth.basic.http.MethodHTTP;

import java.lang.reflect.Method;

public record RouteInfo(MethodHTTP verb, Method method, Object obj) {}
