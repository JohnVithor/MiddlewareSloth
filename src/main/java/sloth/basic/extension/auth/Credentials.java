package sloth.basic.extension.auth;

import java.util.List;

public interface Credentials {

    String username();
    List<String> permissions();
    String getToken();
}
