package se.umu.cs.ads.a1.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Content;
import se.umu.cs.ads.a1.types.Data;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.MessageId;
import se.umu.cs.ads.a1.types.Timestamp;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;

import java.util.List;

import org.restlet.security.User;

import com.google.protobuf.ByteString;

public class GRPCMessenger implements Messenger {
    private final ManagedChannel channel;
    private final MessengerServiceGrpc.MessengerServiceBlockingStub blockingStub;

    public GRPCMessenger() {
        this("localhost", 8080);
    }

    public GRPCMessenger(String host, int port) {
        // set up connection to server
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .maxInboundMessageSize(100 * 1024 * 1024)
                .usePlaintext()
                .build();
        // client side stub
        this.blockingStub = MessengerServiceGrpc.newBlockingStub(channel)
                .withMaxInboundMessageSize(100 * 1024 * 1024);
    }

    @Override
    public void store(Message message) {
        try {
            // build msg stub
            Msg msg = Msg.newBuilder()
                    .setMessageID(message.getId().toString())
                    .setTimestamp(message.getTimestamp().getValue())
                    .setUsername(message.getUsername().toString())
                    .setTopic(message.getTopic().toString())
                    .setContent(message.getContent().toString())
                    .setData(ByteString.copyFrom(message.getData().getValue()))
                    .build();

            StoreRequest request = StoreRequest.newBuilder().setMessage(msg).build();
            StoreResponse response = blockingStub.store(request);

            String responseStr = response.getResponse();
            //System.out.println(responseStr);

        } catch (Exception e) {
            System.out.println("FAHH: " + e.getStackTrace());
        }
    }

    @Override
    public void store(Message[] messages) {
        if (messages == null || messages.length == 0) return;
        try {
            StoreBatchRequest.Builder builder = StoreBatchRequest.newBuilder();
            for (Message message : messages) {
                if (message == null) continue;
                Msg msg = Msg.newBuilder()
                        .setMessageID(message.getId().toString())
                        .setTimestamp(message.getTimestamp().getValue())
                        .setUsername(message.getUsername().toString())
                        .setTopic(message.getTopic().toString())
                        .setContent(message.getContent().toString())
                        .setData(ByteString.copyFrom(message.getData().getValue()))
                        .build();
                builder.addMessages(msg);
            }
            blockingStub.storeBatch(builder.build());
        } catch (Exception e) {
            System.err.println("gRPC batch store error: " + e.getMessage());
        }
    }

    @Override
    public Message retrieve(MessageId message) {
        try {
            RetrieveRequest request = RetrieveRequest.newBuilder().setMessageId(message.toString()).build();
            RetrieveResponse response = blockingStub.retrieve(request);

            Msg protoMsg = response.getMessage();
            if (protoMsg == null || protoMsg.getMessageID().isEmpty()) return null;

            Message msg = new Message(
                    new MessageId(protoMsg.getMessageID()),
                    new Timestamp(protoMsg.getTimestamp()),
                    new Username(protoMsg.getUsername()),
                    new Topic(protoMsg.getTopic()),
                    new Content(protoMsg.getContent()),
                    new Data(protoMsg.getData().toByteArray()));

            return msg;

        } catch (Exception e) {
            System.err.println("gRPC retrieve error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Message[] retrieve(MessageId[] message) {
        if (message == null || message.length == 0) return new Message[0];
        try {
            RetrieveBatchRequest.Builder builder = RetrieveBatchRequest.newBuilder();
            for (MessageId id : message) {
                if (id != null) {
                    builder.addMessageId(id.toString());
                }
            }
            RetrieveBatchResponse response = blockingStub.retrieveBatch(builder.build());
            List<Msg> protoMsgs = response.getMessagesList();
            Message[] returnedMessages = new Message[protoMsgs.size()];
            for (int i = 0; i < protoMsgs.size(); i++) {
                Msg protoMsg = protoMsgs.get(i);
                returnedMessages[i] = new Message(
                        new MessageId(protoMsg.getMessageID()),
                        new Timestamp(protoMsg.getTimestamp()),
                        new Username(protoMsg.getUsername()),
                        new Topic(protoMsg.getTopic()),
                        new Content(protoMsg.getContent()),
                        new Data(protoMsg.getData().toByteArray()));
            }
            return returnedMessages;
        } catch (Exception e) {
            System.err.println("gRPC batch retrieve error: " + e.getMessage());
        }
        return new Message[0];
    }

    @Override
    public void delete(MessageId message) {
        try {
            DeleteRequest request = DeleteRequest.newBuilder().setMessageId(message.toString()).build();
            DeleteResponse response = blockingStub.delete(request);
            String status = response.getResponse();
            System.out.println(status);
        } catch (Exception e) {
            System.err.println("gRPC delete error: " + e.getMessage());
        }

    }

    @Override
    public void delete(MessageId[] messages) {
        if (messages == null || messages.length == 0) return;
        try {
            DeleteBatchRequest.Builder builder = DeleteBatchRequest.newBuilder();
            for (MessageId msgId : messages) {
                if (msgId != null) {
                    builder.addMessageId(msgId.toString());
                }
            }
            blockingStub.deleteBatch(builder.build());
        } catch (Exception e) {
            System.err.println("gRPC batch delete error: " + e.getMessage());
        }
    }

    @Override
    public Topic[] subscribe(Username username, Topic topic) {
        try {
            SubscribeRequest request = SubscribeRequest.newBuilder()
                    .setUsername(username.toString())
                    .setTopic(topic.toString())
                    .build();

            SubscribeResponse response = blockingStub.subscribe(request);
            List<String> topicList = response.getTopicList();

            Topic[] topics = new Topic[topicList.size()];
            for (int i = 0; i < topicList.size(); i++) {
                topics[i] = new Topic(topicList.get(i));
            }
            return topics;

        } catch (Exception e) {
            System.out.println("Subscribe Error: " + e.getStackTrace());
        }
        return null;

    }

    @Override
    public Topic[] unsubscribe(Username username, Topic topic) {
        try {
            SubscribeRequest request = SubscribeRequest.newBuilder()
                    .setUsername(username.toString())
                    .setTopic(topic.toString())
                    .build();

            SubscribeResponse response = blockingStub.unsubscribe(request);
            List<String> topicList = response.getTopicList();

            Topic[] topics = new Topic[topicList.size()];
            for (int i = 0; i < topicList.size(); i++) {
                topics[i] = new Topic(topicList.get(i));
            }
            return topics;

        } catch (Exception e) {
            System.out.println("Unsubscribe Error: " + e.getStackTrace());
        }
        return null;
    }

    @Override
    public Username[] listUsers() {
        try {
            Empty empty = null;
            ListUsersResponse response = blockingStub.listUsers(empty);
            List<String> userList = response.getUserList();

            Username[] users = new Username[userList.size()];
            for (int i = 0; i < userList.size(); i++) {
                users[i] = new Username(userList.get(i));
            }
            return users;

        } catch (Exception e) {
            System.out.println("Error listing users: " + e.getStackTrace());
        }
        return new Username[0];

    }

    @Override
    public Topic[] listTopics() {
        try {
            ListTopicsResponse response = blockingStub.listTopics(null);
            List<String> topicList = response.getTopicList();
            Topic[] topics = new Topic[topicList.size()];

            for (int i = 0; i < topicList.size(); i++) {
                topics[i] = new Topic(topicList.get(i));
            }
            return topics;

        } catch (Exception e) {
            System.out.println("Error listing topics: " + e.getStackTrace());
        }
        return new Topic[0];
    }

    @Override
    public Topic[] listTopics(Username username) {
        try {
            UserRequest request = UserRequest.newBuilder().setUsername(username.toString()).build();
            ListTopicsResponse response = blockingStub.listTopicsUser(request);
            List<String> topicList = response.getTopicList();
            Topic[] topics = new Topic[topicList.size()];

            for (int i = 0; i < topicList.size(); i++) {
                topics[i] = new Topic(topicList.get(i));
            }
            return topics;

        } catch (Exception e) {
            System.out.println("Error listing topics(user): " + e.getStackTrace());
        }
        return new Topic[0];
    }

    @Override
    public Username[] listSubscribers(Topic topic) {
        try {
            TopicRequest request = TopicRequest.newBuilder().setTopic(topic.toString()).build();
            ListUsersResponse response = blockingStub.listSubscribers(request);
            List<String> userList = response.getUserList();
            Username[] users = new Username[userList.size()];

            for (int i = 0; i < userList.size(); i++) {
                users[i] = new Username(userList.get(i));
            }
            return users;
        } catch (Exception e) {
            System.out.println("Error lisitng subs: " + e.getStackTrace());
        }
        return new Username[0];
    }

    @Override
    public MessageId[] listMessages(Username username) {
        try {
            String user = username.toString();
            ListMessagesRequest request = ListMessagesRequest.newBuilder().setUsername(user).build();

            ListMessagesResponse response = blockingStub.listMessages(request);

            List<String> messageList = response.getMessageIDList();
            MessageId[] messages = new MessageId[messageList.size()];
            for (int i = 0; i < messageList.size(); i++) {
                messages[i] = new MessageId(messageList.get(i));
            }
            //System.out.println("List message completed");
            return messages;

        } catch (Exception e) {
            System.out.println("FAHH: " + e.getStackTrace());
        }

        return new MessageId[0];
    }

    @Override
    public MessageId[] listMessages(Topic topic) {
        try {
            String topicStr = topic.toString();
            TopicRequest request = TopicRequest.newBuilder().setTopic(topicStr).build();

            ListMessagesResponse response = blockingStub.listMessagesTopic(request);

            List<String> messageList = response.getMessageIDList();
            MessageId[] messages = new MessageId[messageList.size()];
            for (int i = 0; i < messageList.size(); i++) {
                messages[i] = new MessageId(messageList.get(i));
            }
            //System.out.println("List message completed");
            return messages;

        } catch (Exception e) {
            System.out.println("FAHH: " + e.getStackTrace());
        }

        return new MessageId[0];
    }

}
