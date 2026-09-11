package se.umu.cs.ads.a1.rest;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
                String msgID = (form != null) ? form.getFirstValue("msgID") : null;
                if (msgID == null || msgID.isEmpty()) {
                    return "Error: Missing msgID parameter";
                }
                MessageId messageId = new MessageId(msgID);
                Message msg = messengerBackend.retrieve(messageId);
                // build json object
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode messageNode = mapper.createObjectNode();
                if (msg != null) {
                    if (msg.getId() != null)
                        messageNode.put("msgID", msg.getId().toString());
                    if (msg.getTimestamp() != null)
                        messageNode.put("timeStamp", msg.getTimestamp().toString());
                    if (msg.getUsername() != null)
                        messageNode.put("user", msg.getUsername().toString());
                    if (msg.getTopic() != null)
                        messageNode.put("topic", msg.getTopic().toString());
                    if (msg.getContent() != null)
                        messageNode.put("content", msg.getContent().toString());
                }

                return messageNode.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: Could not retrieve message: " + e.getMessage();
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

        } else if (path.contains("/list-users")) {
            Username[] users = messengerBackend.listUsers();
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();
            if (users != null) {
                for (Username user : users) {
                    arrayNode.add(user.toString());
                }
            }
            return arrayNode.toString();

        } else if (path.contains("/list-topics")) {
            try {
                // check if a username was passed through
                Form form = getReference().getQueryAsForm();
                String username = (form != null) ? form.getFirstValue("user") : null;
                Topic[] topics;
                if (username != null && !username.isEmpty()) {
                    Username user = new Username(username);
                    topics = messengerBackend.listTopics(user);
                } else {
                    topics = messengerBackend.listTopics();
                }

                ObjectMapper mapper = new ObjectMapper();
                ArrayNode arrayNode = mapper.createArrayNode();
                if (topics != null) {
                    for (Topic topic : topics) {
                        arrayNode.add(topic.toString());
                    }
                }
                return arrayNode.toString();

            } catch (Exception e) {
                return "Error: Could not list topics: " + e.getMessage();
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
                String topicStr = (json != null && json.has("topic")) ? json.get("topic").asText() : null;
                String msgID = (json != null && json.has("msgID")) ? json.get("msgID").asText() : null;

                MessageId msgId = (msgID != null && !msgID.isEmpty()) ? new MessageId(msgID) : MessageId.construct();
                Timestamp timestamp = Timestamp.now();
                Content content = (message != null) ? new Content(message) : null;
                Username user = (username != null) ? new Username(username) : null;
                Topic topic = (topicStr != null) ? new Topic(topicStr) : null;
                Message msg = new Message(msgId, timestamp, user, topic, content, Data.EMPTY);

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
                Form form = getReference().getQueryAsForm();
                String msgID = form.getFirstValue("msgID");
                MessageId messageId = new MessageId(msgID);
                messengerBackend.delete(messageId);
                return "Message " + msgID + " deleted successfully";
            } catch (Exception e) {
                Form form = getReference().getQueryAsForm();
                String msgID = form.getFirstValue("msgID");
                return "Error: Could not delete message: " + msgID;
            }
        }
        return "Not Found";
    }
}
