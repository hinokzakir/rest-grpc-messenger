package se.umu.cs.ads.a1.rest;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.MessageId;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;

// import the restlet library
import org.restlet.*;

public class RESTMessenger implements Messenger {

    //----------------------------------------------------------
    // message interface
    @Override
    public void store(Message message) {
        throw new UnsupportedOperationException("Unimplemented method 'store'");

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

    //----------------------------------------------------------
    // subscription interface
    @Override
    public Topic[] subscribe(Username username, Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'subscribe'");
    }

    @Override
    public Topic[] unsubscribe(Username username, Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribe'");
    }

    //----------------------------------------------------------
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
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }

    @Override
    public MessageId[] listMessages(Topic topic) {
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }
}