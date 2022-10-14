package sloth.basic.extension.protocolplugin;

import java.io.IOException;

public interface Protocol {

    void init(int port) throws IOException;
    Connection connect() throws IOException;
}
