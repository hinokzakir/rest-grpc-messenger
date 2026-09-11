package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;
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

    @Get("json")
    public String represent(Representation entity) {
        String path = getReference().getPath();
        Form form = new Form(entity);
        if (path.contains("/retrieve")) {
            try {
                String payload = form.getFirstValue("msgID");
                MessageId msgID = new MessageId(payload);
                Message msg = messengerBackend.retrieve(msgID);
                return msg.getContent().toString();
            } catch (Exception e) {
                return "Error: Could not retrieve message";
            }
        } else if (path.contains("/list-messages")) {
            try {
                String payload = form.getFirstValue("user");
                Username user = new Username(payload);
                MessageId[] messages = messengerBackend.listMessages(user);

                String result = "";
                for (MessageId id : messages) {
                    result += id.toString() + "\n";
                }
                return result;
            } catch (Exception e) {
                String payload = form.getFirstValue("user");
                return "Error: Invalid username: " + payload;
            }
        } else {
            return "Not Found";
        }
    }

    @Post("json")
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

    @Delete("json")
    public String delete(Representation entity) {
        String path = getReference().getPath();
        if (path.contains("/delete")) {
            try {
                Form form = new Form(entity);
                String msgID = form.getFirstValue("msgID");
                MessageId messageId = new MessageId(msgID);
                messengerBackend.delete(messageId);
                return "Message " + msgID + " deleted successfully";
            } catch (Exception e) {
                Form form = new Form(entity);
                String msgID = form.getFirstValue("msgID");
                return "Error: Could not delete message: " + msgID;
            }
        }
        return "Not Found";
    }
}
