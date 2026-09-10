package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.*;

public class RESTResource extends ServerResource {
    private Messenger messengerBackend;

    @Override
    protected void doInit() {
        super.doInit();
        this.messengerBackend = (Messenger) getContext().getAttributes().get("messenger");
    }

    @Get("text/plain")
    public String represent(String message) {
        String path = getReference().getPath();
        if (path.contains("/hello")) {
            return "hello from rest-server";
        }
        if (path.contains("/retrieve")) {
            try {
                MessageId msgID = new MessageId(message);
                Message msg = messengerBackend.retrieve(msgID);
                return msg.getContent().toString();
            } catch (Exception e) {
                return "Error: Could not retrieve message";
            }
        }
        return "Not Found";
    }

    @Post("text/plain")
    public String store(String message) {
        String path = getReference().getPath();
        if (path.contains("/store")) {
            try {
                // create msg id
                MessageId msgId = MessageId.construct();
                Content content = new Content(message);
                Message msg = new Message(msgId, null, null, null, content, null);
                messengerBackend.store(msg);
                return "Message:  " + message + " stored successfully, ID: " + msgId.toString();
            } catch (Exception E) { // catch if the backend cannot store
                return "Error: Could not store message";
            }
        }
        return "Not Found";
    }
}
