package se.umu.cs.ads.a1.rest;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import se.umu.cs.ads.a1.backend.InMemoryMessengerBackEnd;
import se.umu.cs.ads.a1.interfaces.Messenger;

public class RESTServer {

    private final Component component;
    private final Messenger backend;

    public RESTServer(int port) {
        this.backend = new InMemoryMessengerBackEnd();
        this.component = new Component();
        this.component.getServers().add(Protocol.HTTP, port);

        //this attaches router to the default host
        this.component.getDefaultHost().attachDefault(new MessengerApplication());
    }

    public void start() throws Exception {
        component.start();
        System.out.println("RESTServer running on HTTP port: " + component.getServers().get(0).getPort());
        System.out.println("Test URL: http://localhost:" + component.getServers().get(0).getPort() + "/ping");
    }

    public void stop() throws Exception {
        component.stop();
    }

    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 8000;
            RESTServer server = new RESTServer(port);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // class for Restlet routing
    private static class MessengerApplication extends Application {
        @Override
        public Restlet createInboundRoot() {
            Router router = new Router(getContext());
            // Test route
            router.attach("/ping", RESTResource.class);
            return router;
        }
    }
}
