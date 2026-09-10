package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.representation.Representation;

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
    public String represent(String payload) {
        String path = getReference().getPath();
        if (path.contains("/hello")) {
            return "hello from rest-server";
        }
        if (path.contains("/retrieve")) {
            try {
                MessageId msgID = new MessageId(payload);
                Message msg = messengerBackend.retrieve(msgID);
                return msg.getContent().toString();
            } catch (Exception e) {
                return "Error: Could not retrieve message";
            }
        } else if (path.contains("/list-messages")) {
            try {
                Username user = new Username(payload);
                MessageId[] messages = messengerBackend.listMessages(user);

                String result = "";
                for (MessageId id : messages) {
                    result += id.toString() + "\n";
                }
                return result;
            } catch (Exception e) {
                return "Error: Invalid username: " + payload;
            }
        } else {
            return "Not Found";
        }
    }

    @Post("text/plain")
    public String store(Representation entity) {
        String path = getReference().getPath();
        if (path.contains("/store")) {
            try {
                Form form = new Form(entity);
                String message = form.getFirstValue("message");
                String username = form.getFirstValue("user");
                // create msg id
                MessageId msgId = MessageId.construct();
                Content content = new Content(message);
                Username user = new Username(username);
                Message msg = new Message(msgId, null, user, null, content, null);
                messengerBackend.store(msg);
                return "Message:  " + message + " stored successfully, ID: " + msgId.toString() + ", username: " + msg.getUsername();
            } catch (Exception E) { // catch if the backend cannot store
                return "Error: Could not store message: " + E.toString();
            }
                        
        }
        return "Not Found";
    }
}
