package se.umu.cs.ads.a1.grpc;

import com.google.longrunning.DeleteOperationRequest;
import com.google.protobuf.ByteString;
import com.google.rpc.context.AttributeContext.ResponseOrBuilder;

import io.grpc.stub.StreamObserver;
import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.*;

public class MessengerService extends MessengerServiceGrpc.MessengerServiceImplBase {
    private final Messenger backend;

    public MessengerService(Messenger backend) {
        this.backend = backend;
    }

    @Override
    public void listMessages(ListMessagesRequest request, StreamObserver<ListMessagesResponse> responseObserver) {
        // first create a Username object out of the request
        Username username = new Username(request.getUsername());
        MessageId[] messages = backend.listMessages(username);

        // create response out of messages and return to client
        ListMessagesResponse.Builder builder = ListMessagesResponse.newBuilder();
        for (MessageId message : messages) {
            builder.addMessageID(message.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listMessagesTopic(TopicRequest request, StreamObserver<ListMessagesResponse> responseObserver) {
        // first create a Username object out of the request
        Topic topic = new Topic(request.getTopic());
        MessageId[] messages = backend.listMessages(topic);

        // create response out of messages and return to client
        ListMessagesResponse.Builder builder = ListMessagesResponse.newBuilder();
        for (MessageId message : messages) {
            builder.addMessageID(message.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void store(StoreRequest request, StreamObserver<StoreResponse> responseObserver) {
        Message message = new Message(
                new MessageId(request.getMessage().getMessageID()),
                new Timestamp(request.getMessage().getTimestamp()),
                new Username(request.getMessage().getUsername()),
                new Topic(request.getMessage().getTopic()),
                new Content(request.getMessage().getContent()),
                new Data(request.getMessage().getData().toByteArray()));

        backend.store(message);
        StoreResponse response = StoreResponse.newBuilder()
                .setResponse("OK")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        MessageId id = new MessageId(request.getMessageId().toString());

        backend.delete(id);
        DeleteResponse response = DeleteResponse.newBuilder()
                .setResponse("Successfully Deleted Message: " + id.toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void retrieve(RetrieveRequest request, StreamObserver<RetrieveResponse> responseObserver) {

        MessageId id = new MessageId(request.getMessageId().toString());
        Message message = backend.retrieve(id);

        Msg msg = Msg.newBuilder()
            .setMessageID(message.getId().toString())
            .setTimestamp(message.getTimestamp().getValue())
            .setUsername(message.getUsername().toString())
            .setTopic(message.getTopic().toString())
            .setContent(message.getContent().toString())
            .setData(ByteString.copyFrom(message.getData().getValue()))
            .build();
        
        backend.store(message);
        RetrieveResponse response = RetrieveResponse.newBuilder()
                .setMessage(msg)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribe(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        Username user = new Username(request.getUsername().toString());
        Topic topic = new Topic(request.getTopic().toString());

        Topic[] topics = backend.subscribe(user, topic);

        SubscribeResponse.Builder builder = SubscribeResponse.newBuilder();
        for(Topic t : topics){
            builder.addTopic(t.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void unsubscribe(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        Username user = new Username(request.getUsername().toString());
        Topic topic = new Topic(request.getTopic().toString());

        Topic[] topics = backend.unsubscribe(user, topic);

        SubscribeResponse.Builder builder = SubscribeResponse.newBuilder();
        for(Topic t : topics){
            builder.addTopic(t.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listUsers(Empty request, StreamObserver<ListUsersResponse> responseObserver) {
        Username[] users = backend.listUsers();

        ListUsersResponse.Builder builder = ListUsersResponse.newBuilder();

        for(Username user : users){
            builder.addUser(user.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listTopics(Empty request, StreamObserver<ListTopicsResponse> responseObserver) {
        Topic[] topics = backend.listTopics();

        ListTopicsResponse.Builder builder = ListTopicsResponse.newBuilder();

        for(Topic topic : topics){
            builder.addTopic(topic.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listTopicsUser(UserRequest request, StreamObserver<ListTopicsResponse> responseObserver) {
        Username user =  new Username(request.getUsername().toString());

        Topic[] topics = backend.listTopics(user);

        ListTopicsResponse.Builder builder = ListTopicsResponse.newBuilder();

        for(Topic topic : topics){
            builder.addTopic(topic.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        
    }

    @Override 
    public void listSubscribers(TopicRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        Topic topic = new Topic(request.getTopic().toString());
        Username[] subscribers = backend.listSubscribers(topic);

        ListUsersResponse.Builder builder = ListUsersResponse.newBuilder();

        for(Username user : subscribers){
            builder.addUser(user.toString());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}