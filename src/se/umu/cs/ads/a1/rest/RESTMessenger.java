package se.umu.cs.ads.a1.rest;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.*;
import se.umu.cs.ads.a1.types.Data;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.security.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RESTMessenger implements Messenger {
    private String serverUrl = "http://localhost:8000";
    private final ObjectMapper mapper = new ObjectMapper();
    private ClientResource client;

    public RESTMessenger() {
        this.client = new ClientResource(serverUrl);
    }

    public RESTMessenger(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new ClientResource(serverUrl);
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
            client.setReference(serverUrl + "/store");
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
            client.setReference(serverUrl + "/retrieve?msgID=" + msgID);
            Representation response = client.get();

            // build message from json to message object
            JsonNode jsonNode = mapper.readTree(response.getText());

            MessageId messageId = (jsonNode.has("msgID") && !jsonNode.get("msgID").isNull())
                    ? new MessageId(jsonNode.get("msgID").asText())
                    : message;
            Timestamp timeStamp = (jsonNode.has("timeStamp") && !jsonNode.get("timeStamp").isNull())
                    ? new Timestamp(jsonNode.get("timeStamp").asLong())
                    : Timestamp.now();
            Username user = (jsonNode.has("user") && !jsonNode.get("user").isNull())
                    ? new Username(jsonNode.get("user").asText())
                    : null;
            Topic topic = (jsonNode.has("topic") && !jsonNode.get("topic").isNull())
                    ? new Topic(jsonNode.get("topic").asText())
                    : null;
            Content content = (jsonNode.has("content") && !jsonNode.get("content").isNull())
                    ? new Content(jsonNode.get("content").asText())
                    : null;
            Data data = (jsonNode.has("data") && !jsonNode.get("data").isNull()
                    && jsonNode.get("data").binaryValue() != null)
                            ? new Data(jsonNode.get("data").binaryValue())
                            : Data.EMPTY;

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

            client.setReference(serverUrl + "/delete?msgID=" + msgID);
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
        for (MessageId message : messages) {
            delete(message);
        }
    }

    // ----------------------------------------------------------
    // subscription interface
    @Override
    public Topic[] subscribe(Username username, Topic topic) {
        String user = username.toString();
        String topicStr = topic.toString();
        try {
            client.setReference(serverUrl + "/subscribe");

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("user", user);
            jsonNode.put("topic", topicStr);
            Representation response = client.post(jsonNode);

            if (response != null) {
                System.out.println("Subscribe Server Response: " + response.getText());
            }

            // create a topic array of response
            JsonNode jsonArray = mapper.readTree(response.getText());
            Topic[] topics = new Topic[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                topics[i] = new Topic(jsonArray.get(i).asText());
            }
            client.release();
            return topics;
        } catch (Exception e) {
            System.err.println("Error calling /subscribe endpoint: " + e.getMessage());
            e.printStackTrace();
        }

        return new Topic[0];
    }

    @Override
    public Topic[] unsubscribe(Username username, Topic topic) {
        String user = username.toString();
        String topicStr = topic.toString();
        try {
            client.setReference(serverUrl + "/unsubscribe");

            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("user", user);
            jsonNode.put("topic", topicStr);
            Representation response = client.post(jsonNode);

            if (response != null) {
                System.out.println("Subscribe Server Response: " + response.getText());
            }

            // create a topic array of response
            JsonNode jsonArray = mapper.readTree(response.getText());
            Topic[] topics = new Topic[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                topics[i] = new Topic(jsonArray.get(i).asText());
            }
            client.release();
            return topics;
        } catch (Exception e) {
            System.err.println("Error calling /subscribe endpoint: " + e.getMessage());
            e.printStackTrace();
        }

        return new Topic[0];
    }

    // ----------------------------------------------------------
    // query interface
    @Override
    public Username[] listUsers() {
        try {
            client.setReference(serverUrl + "/list-users");
            Representation response = client.get();
            if (response != null) {
                String text = response.getText();
                JsonNode jsonArray = mapper.readTree(text);
                client.release();

                if (jsonArray != null && jsonArray.isArray()) {
                    Username[] users = new Username[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        users[i] = new Username(jsonArray.get(i).asText());
                    }
                    return users;
                }

            }
        } catch (Exception e) {
            System.err.println("Error calling /list-users endpoint: " + e.getMessage());
        }
        return new Username[0];
    }

    @Override
    public Topic[] listTopics() {
        try {
            client.setReference(serverUrl + "/list-topics");
            Representation response = client.get();
            if (response != null) {
                String text = response.getText();
                JsonNode jsonArray = mapper.readTree(text);
                client.release();

                if (jsonArray != null && jsonArray.isArray()) {
                    Topic[] topics = new Topic[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        topics[i] = new Topic(jsonArray.get(i).asText());
                    }
                    return topics;
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling /list-topics endpoint: " + e.getMessage());
            e.printStackTrace();
        }
        return new Topic[0];
    }

    @Override
    public Topic[] listTopics(Username username) {
        if (username == null)
            return new Topic[0];
        try {
            String user = username.toString();
            client.setReference(serverUrl + "/list-topics?user=" + user);
            Representation response = client.get();
            if (response != null) {
                String text = response.getText();
                JsonNode jsonArray = mapper.readTree(text);
                client.release();

                if (jsonArray != null && jsonArray.isArray()) {
                    Topic[] topics = new Topic[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        topics[i] = new Topic(jsonArray.get(i).asText());
                    }
                    return topics;
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling /list-topics endpoint: " + e.getMessage());
            e.printStackTrace();
        }
        return new Topic[0];
    }

    @Override
    public Username[] listSubscribers(Topic topic) {
        if (topic == null)
            return new Username[0];
        try {
            String topicStr = topic.toString();
            client.setReference(serverUrl + "/list-subscribers?topic=" + topicStr);
            Representation response = client.get();

            if (response != null) {
                String text = response.getText();
                JsonNode jsonArray = mapper.readTree(text);
                client.release();
                if (jsonArray != null && jsonArray.isArray()) {
                    Username[] users = new Username[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        users[i] = new Username(jsonArray.get(i).asText());
                    }
                    return users;
                }

            }

        } catch (Exception e) {
            System.out.println("Error calling /list-subscribers endpoint: " + e.getMessage());
        }
        return new Username[0];
    }

    @Override
    public MessageId[] listMessages(Username username) {
        if (username == null)
            return new MessageId[0];

        try {
            String user = username.toString();

            client.setReference(serverUrl + "/list-messages?user=" + user);
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