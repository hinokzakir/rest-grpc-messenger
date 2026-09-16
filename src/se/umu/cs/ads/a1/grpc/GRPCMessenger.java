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

import com.google.protobuf.ByteString;

public class GRPCMessenger implements Messenger {
    private final ManagedChannel channel;
    private final MessengerServiceGrpc.MessengerServiceBlockingStub blockingStub;

    public GRPCMessenger() {
        this("localhost", 8080);
    }

    public GRPCMessenger(String host, int port) {
        // set up connection to server
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        // client side stub
        this.blockingStub = MessengerServiceGrpc.newBlockingStub(channel);
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
            System.out.println(responseStr);

        } catch (Exception e) {
            System.out.println("FAHH: " + e.getStackTrace());
        }
    }

    @Override
    public void store(Message[] messages) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'store'");
    }

    @Override
    public Message retrieve(MessageId message) {
        try {
            RetrieveRequest request = RetrieveRequest.newBuilder().setMessageId(message.toString()).build();
            RetrieveResponse response = blockingStub.retrieve(request);

            Msg protoMsg = response.getMessage();

            Message msg = new Message(
                    new MessageId(protoMsg.getMessageID()),
                    new Timestamp(protoMsg.getTimestamp()),
                    new Username(protoMsg.getUsername()),
                    new Topic(protoMsg.getTopic()),
                    new Content(protoMsg.getContent()),
                    new Data(protoMsg.getData().toByteArray()));

            return msg;

        } catch (Exception e) {
            System.out.println("FAHH: " + e.getStackTrace());
        }
        return null;
    }

    @Override
    public Message[] retrieve(MessageId[] message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retrieve'");
    }

    @Override
    public void delete(MessageId message) {
        try {
            DeleteRequest request = DeleteRequest.newBuilder().setMessageId(message.toString()).build();
            DeleteResponse response = blockingStub.delete(request);
            String status = response.getResponse();
            System.out.println(status);
        } catch (Exception e) {
            System.out.println("FAAAHHH: " + e.getStackTrace());
        }

    }

    @Override
    public void delete(MessageId[] messages) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Topic[] subscribe(Username username, Topic topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subscribe'");
    }

    @Override
    public Topic[] unsubscribe(Username username, Topic topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribe'");
    }

    @Override
    public Username[] listUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listUsers'");
    }

    @Override
    public Topic[] listTopics() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listTopics'");
    }

    @Override
    public Topic[] listTopics(Username username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listTopics'");
    }

    @Override
    public Username[] listSubscribers(Topic topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listSubscribers'");
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
            System.out.println("List message completed");
            return messages;

        } catch (Exception e) {
            System.out.println("FAHH: " + e.getStackTrace());
        }

        return new MessageId[0];
    }

    @Override
    public MessageId[] listMessages(Topic topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }

}
