package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
    public String represent() {
        String path = getReference().getPath();
        if (path.contains("/retrieve")) {
            try {
                Form form = getReference().getQueryAsForm();
                String msgID = form.getFirstValue("msgID");
                MessageId messageId = new MessageId(msgID);
                Message msg = messengerBackend.retrieve(messageId);
                return msg.getContent().toString();
            } catch (Exception e) {
                return "Error: Could not retrieve message";
            }
        } else if (path.contains("/list-messages")) {
            try {
                Form form = getReference().getQueryAsForm();
                String username = form.getFirstValue("user");
                Username user = new Username(username);
                MessageId[] messages = messengerBackend.listMessages(user);

                ObjectMapper mapper = new ObjectMapper();
                ArrayNode arrayNode = mapper.createArrayNode();
                if (messages != null) {
                    for (MessageId id : messages) {
                        arrayNode.add(id.toString());
                    }
                }
                return arrayNode.toString();
            } catch (Exception e) {
                Form form = getReference().getQueryAsForm();
                String username = form.getFirstValue("user");
                return "Error: Invalid username: " + username;
            }
        } else {
            return "Not Found";
        }
    }

    @Post("json")
    public String store(JsonNode json) {
        String path = getReference().getPath();
        if (path.contains("/store")) {
            try {
                String message = (json != null && json.has("message")) ? json.get("message").asText() : null;
                String username = (json != null && json.has("user")) ? json.get("user").asText() : null;
                String msgID = (json != null && json.has("msgID")) ? json.get("msgID").asText() : null;

                MessageId msgId = (msgID != null && !msgID.isEmpty()) ? new MessageId(msgID) : MessageId.construct();
                Content content = new Content(message);
                Username user = new Username(username);
                Message msg = new Message(msgId, null, user, null, content, null);

                messengerBackend.store(msg);
                return "Message: " + message + " stored successfully, ID: " + msgId.toString() + ", username: "
                        + msg.getUsername();
            } catch (Exception E) {
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
