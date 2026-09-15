package se.umu.cs.ads.a1.grpc;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.MessageId;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;

public class GRPCMessenger implements Messenger {

    @Override
    public void store(Message message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'store'");
    }

    @Override
    public void store(Message[] messages) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'store'");
    }

    @Override
    public Message retrieve(MessageId message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retrieve'");
    }

    @Override
    public Message[] retrieve(MessageId[] message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retrieve'");
    }

    @Override
    public void delete(MessageId message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }

    @Override
    public MessageId[] listMessages(Topic topic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }
    
}
