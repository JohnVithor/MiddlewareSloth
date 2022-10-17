package sloth.basic.extension.auth;

import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.extension.RegistrationConfiguration;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.util.RouteInfos;

import java.util.HashSet;
import java.util.regex.Pattern;

public class SimpleAuth implements HTTPAuth {

    private final HashSet<String> routes = new HashSet<>();
    private final Pattern pattern;

    public SimpleAuth() {
        pattern = Pattern.compile(".*");
    }

    public SimpleAuth(String p) {
        pattern = Pattern.compile(p);
    }

    @Override
    public void consume(String route, RouteInfos infos) {
        if(pattern.matcher(route).matches()){
            routes.add(route);
        }
    }

    @Override
    public boolean check(String route) {
        return routes.contains(route);
    }
}
