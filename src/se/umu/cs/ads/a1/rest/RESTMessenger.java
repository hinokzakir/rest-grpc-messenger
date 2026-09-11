package se.umu.cs.ads.a1.rest;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.*;
import se.umu.cs.ads.a1.types.Data;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RESTMessenger implements Messenger {
    private String serverUrl = "http://localhost:8000";
    private final ObjectMapper mapper = new ObjectMapper();

    public RESTMessenger() {
    }

    public RESTMessenger(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // ----------------------------------------------------------
    // message interface
    @Override
    public void store(Message message) {
        if (message == null)
            return;

        try {
            // builds JSON object using Jackson
            ObjectNode jsonNode = mapper.createObjectNode();

            if (message.getContent() != null)
                jsonNode.put("message", message.getContent().toString());
            if (message.getUsername() != null)
                jsonNode.put("user", message.getUsername().toString());
            if (message.getTopic() != null)
                jsonNode.put("topic", message.getTopic().toString());
            if (message.getId() != null)
                jsonNode.put("msgID", message.getId().toString());

            // post JSON payload to /store endpoint
            ClientResource client = new ClientResource(serverUrl + "/store");
            Representation response = client.post(jsonNode);

            if (response != null) {
                System.out.println("Store Server Response: " + response.getText());
            }
            client.release();
        } catch (Exception e) {
            System.err.println("Error calling /store endpoint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void store(Message[] messages) {
        for (Message message : messages) {
            store(message);
        }
    }

    @Override
    public Message retrieve(MessageId message) {
        if (message == null)
            return null;

        try {
            String msgID = message.toString();
            ClientResource client = new ClientResource(serverUrl + "/retrieve?msgID=" + msgID);
            Representation response = client.get();

            // build message from json to message object
            JsonNode jsonNode = mapper.readTree(response.getText());

            MessageId messageId = (jsonNode.has("msgID") && !jsonNode.get("msgID").isNull())
                    ? new MessageId(jsonNode.get("msgID").asText()) : message;
            Timestamp timeStamp = (jsonNode.has("timeStamp") && !jsonNode.get("timeStamp").isNull())
                    ? new Timestamp(jsonNode.get("timeStamp").asLong()) : Timestamp.now();
            Username user = (jsonNode.has("user") && !jsonNode.get("user").isNull())
                    ? new Username(jsonNode.get("user").asText()) : null;
            Topic topic = (jsonNode.has("topic") && !jsonNode.get("topic").isNull())
                    ? new Topic(jsonNode.get("topic").asText()) : null;
            Content content = (jsonNode.has("content") && !jsonNode.get("content").isNull())
                    ? new Content(jsonNode.get("content").asText()) : null;
            Data data = (jsonNode.has("data") && !jsonNode.get("data").isNull() && jsonNode.get("data").binaryValue() != null)
                    ? new Data(jsonNode.get("data").binaryValue()) : Data.EMPTY;

            return new Message(messageId, timeStamp, user, topic, content, data);

        } catch (Exception e) {
            System.err.println("Error calling /retrieve endpoint: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Message[] retrieve(MessageId[] messages) {
        Message[] returnedMessages = new Message[messages.length];
        for (int i = 0; i < messages.length; i++) {
            returnedMessages[i] = retrieve(messages[i]);
        }
        return returnedMessages;
    }

    @Override
    public void delete(MessageId message) {
        if (message == null)
            return;

        try {
            String msgID = message.toString();

            ClientResource client = new ClientResource(serverUrl + "/delete?msgID=" + msgID);
            Representation response = client.delete();

            if (response != null) {
                System.out.println("Delete Server Response: " + response.getText());
            }
            client.release();
        } catch (Exception e) {
            System.err.println("Error calling /delete endpoint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(MessageId[] messages) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    // ----------------------------------------------------------
    // subscription interface
    @Override
    public Topic[] subscribe(Username username, Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'subscribe'");
    }

    @Override
    public Topic[] unsubscribe(Username username, Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribe'");
    }

    // ----------------------------------------------------------
    // query interface
    @Override
    public Username[] listUsers() {
        throw new UnsupportedOperationException("Unimplemented method 'listUsers'");
    }

    @Override
    public Topic[] listTopics() {
        throw new UnsupportedOperationException("Unimplemented method 'listTopics'");
    }

    @Override
    public Topic[] listTopics(Username username) {
        throw new UnsupportedOperationException("Unimplemented method 'listTopics'");
    }

    @Override
    public Username[] listSubscribers(Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'listSubscribers'");
    }

    @Override
    public MessageId[] listMessages(Username username) {
        if (username == null)
            return new MessageId[0];

        try {
            String user = username.toString();

            ClientResource client = new ClientResource(serverUrl + "/list-messages?user=" + user);
            Representation response = client.get();

            if (response != null) {
                String text = response.getText();
                JsonNode jsonArray = mapper.readTree(text);
                client.release();

                if (jsonArray != null && jsonArray.isArray()) {
                    MessageId[] result = new MessageId[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        result[i] = new MessageId(jsonArray.get(i).asText());
                    }
                    return result;
                }
            }
            client.release();
        } catch (Exception e) {
            System.err.println("Error calling /list-messages endpoint: " + e.getMessage());
            e.printStackTrace();
        }
        return new MessageId[0];
    }

    @Override
    public MessageId[] listMessages(Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }
}