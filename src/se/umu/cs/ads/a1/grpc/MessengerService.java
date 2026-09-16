package se.umu.cs.ads.a1.grpc;

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
}