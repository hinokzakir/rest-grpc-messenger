package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import se.umu.cs.ads.a1.interfaces.Messenger;

public class RESTResource extends ServerResource {
    private Messenger messenger;

    @Override
    protected void doInit() {
        super.doInit();
        this.messenger = (Messenger) getContext().getAttributes().get("messenger");
    }

    @Get("text/plain")
    public String represent() {
        String path = getReference().getPath();
        if (path.contains("/ping")) {
            return "pong";
        }
        if (path.contains("/hello")) {
            return "hello from rest-server";
        }
        return "Not Found";
    }
}
