package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RESTResource extends ServerResource {

    @Get("text/plain")
    public String represent() {
        return "hello from rest-server";
    }
}
