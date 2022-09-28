package sloth.basic.util;

import sloth.basic.http.MethodHTTP;

import java.lang.reflect.Method;

public record RouteInfo(MethodHTTP verb, String content_type, Method method, Object obj) {}
