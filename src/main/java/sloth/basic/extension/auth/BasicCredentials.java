package sloth.basic.extension.auth;

import java.util.List;

public class BasicCredentials implements Credentials {

    private final String username;
    private final List<String> permissions;

    public BasicCredentials(String username, List<String> permissions) {
        this.username = username;
        this.permissions = permissions;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public String getToken() {
        return username.toUpperCase();
    }

}
