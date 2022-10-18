package sloth.basic.extension.auth;

import sloth.basic.annotations.route.Body;
import sloth.basic.annotations.route.MethodMapping;
import sloth.basic.annotations.route.RequestMapping;
import sloth.basic.error.RemotingException;
import sloth.basic.http.data.MethodHTTP;
import sloth.basic.http.util.RouteInfos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

@RequestMapping(path = "auth")
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
        if (route.equals("/auth/login")) {
            return false;
        }
        return routes.contains(route);
    }

    @Override
    public Credentials authenticate(String username, String password) throws AuthException {
        if (username.equals("admin") && password.equals("admin")) {
            return new BasicCredentials(username, new ArrayList<>());
        }
        throw new AuthException("Credenciais inválidas");
    }

    static class JsonCredentials {
        public String username;
        public String password;
    }

    @MethodMapping(method = MethodHTTP.POST, path = "login")
    public String login(@Body JsonCredentials body) throws RemotingException {
        try {
            Credentials credentials = authenticate(body.username, body.password);
            return credentials.getToken();
        } catch (AuthException e) {
            throw new RemotingException(403, "Username or password not valid");
        }
    }
}
