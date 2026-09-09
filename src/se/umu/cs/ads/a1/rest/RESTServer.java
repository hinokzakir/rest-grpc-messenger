package se.umu.cs.ads.a1.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;
import se.umu.cs.ads.a1.backend.InMemoryMessengerBackEnd;
import se.umu.cs.ads.a1.interfaces.Messenger;
import org.restlet.Application;

// first draft of a rest server

public class RESTServer extends Application {

    private final Component component;
    private final Messenger backend;

    public RESTServer(int port) {
        this.backend = new InMemoryMessengerBackEnd();
        this.component = new Component();
        this.component.getServers().add(Protocol.HTTP, port);
        //this.component.getServers().get(0).setAddress("localhost"); // 127.0.0.1
    }

    public void start() throws Exception {
        component.start();
        System.out.println("RESTServer running on HTTP port: " + component.getServers().get(0).getPort());
        //System.out.println("RESTServer running on IP: " + component.getServers().get(0).getAddress());
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
}
