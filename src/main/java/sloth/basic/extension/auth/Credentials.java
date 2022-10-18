package sloth.basic.extension.auth;

import java.util.List;

public interface Credentials {

    String getUsername();
    List<String> getPermissions();
    String getToken();
}
