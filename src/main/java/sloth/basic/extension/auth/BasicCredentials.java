package sloth.basic.extension.auth;

import java.util.List;

public record BasicCredentials(String username, List<String> permissions) implements Credentials {

    @Override
    public String getToken() {
        return username.toUpperCase();
    }

}
