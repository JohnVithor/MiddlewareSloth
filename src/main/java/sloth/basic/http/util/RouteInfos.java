package sloth.basic.http.util;

import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.http.data.MethodHTTP;

import java.util.HashMap;
import java.util.Optional;

public class RouteInfos {

    private final HashMap<MethodHTTP, Route> methods = new HashMap<>();

    public void add(Route route) throws MiddlewareConfigurationException {
        if (methods.containsKey(route.verb())){
            throw new MiddlewareConfigurationException("Method " + route.verb() + " already exists");
        }
        methods.put(route.verb(), route);
    }

    public Optional<Route> get(MethodHTTP verb) {
        return Optional.ofNullable(methods.get(verb));
    }
}
