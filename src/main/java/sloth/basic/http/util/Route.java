package sloth.basic.http.util;

import sloth.basic.http.data.MethodHTTP;

import java.lang.reflect.Method;

public record Route(MethodHTTP verb, String content_type, Method method, Object obj) {}
