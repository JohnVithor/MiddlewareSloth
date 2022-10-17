package sloth.basic.extension;

import sloth.basic.http.util.RouteInfos;
import sloth.basic.marshaller.IdentifiedSizeable;

public interface RegistrationConfiguration<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> {

    void consume(String route, RouteInfos infos);
}
