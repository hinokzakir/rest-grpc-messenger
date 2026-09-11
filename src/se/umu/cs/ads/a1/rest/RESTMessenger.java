package se.umu.cs.ads.a1.rest;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.MessageId;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;

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
        throw new UnsupportedOperationException("Unimplemented method 'store'");
    }

    @Override
    public Message retrieve(MessageId message) {
        throw new UnsupportedOperationException("Unimplemented method 'retrieve'");
    }

    @Override
    public Message[] retrieve(MessageId[] messages) {
        throw new UnsupportedOperationException("Unimplemented method 'retrieve'");
    }

    @Override
    public void delete(MessageId message) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
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