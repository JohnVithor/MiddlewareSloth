package sloth.basic.annotations;

import sloth.basic.http.MethodHTTP;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodMapping {
    MethodHTTP method();
    String path() default "";
    String content_type() default "application/json";
}
