package se.umu.cs.ads.a1.clients;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;
import se.umu.cs.ads.a1.util.Util;

public class LogicTest {
  private final Messenger messenger;

  // ----------------------------------------------------------
  private LogicTest(Messenger messenger) {
    this.messenger = messenger;
  }

  // ----------------------------------------------------------
  // example logic test
  public void testStoreAndDelete(Message message) {
    Username username = message.getUsername();

    int nrMessagesBeforeStore = messenger.listMessages(username).length;
    messenger.store(message);
    int nrMessagesAfterStore = messenger.listMessages(username).length;
    if (nrMessagesAfterStore != (nrMessagesBeforeStore + 1))
      throw new IllegalStateException("testStoreAndDelete(): store failure");

    int nrMessagesBeforeDelete = nrMessagesAfterStore;
    messenger.delete(message.getId());
    int nrMessagesAfterDelete = messenger.listMessages(username).length;
    if (nrMessagesAfterDelete != (nrMessagesBeforeDelete - 1))
      throw new IllegalStateException("testStoreAndDelete(): delete failure");
  }

  public void testSubscribeUnsubscribe(Message message) {
    messenger.store(message);
    Username user = message.getUsername();
    Topic topic = message.getTopic();
    int nrSubscribersBeforeSubscribe = messenger.listSubscribers(topic).length;
    messenger.subscribe(user, topic);
    int nrSubscribersAfterSubscribe = messenger.listSubscribers(topic).length;
    if (nrSubscribersAfterSubscribe != (nrSubscribersBeforeSubscribe + 1)) {
      throw new IllegalStateException("testSubscribeUnsubscribe): subscribe failure");
    }

    int nrSubscribersBeforeUnsubscribe = nrSubscribersAfterSubscribe;
    messenger.unsubscribe(user, topic);
    int nrSubscribersAfterUnsubscribe = messenger.listSubscribers(topic).length;
    if (nrSubscribersAfterUnsubscribe != (nrSubscribersBeforeUnsubscribe - 1)) {
      throw new IllegalStateException("testSubscribeUnsubscribe): unsubscribe failure");
    }
    messenger.delete(message.getId());
  }

  public void testRetrieve(Message message) {
    messenger.store(message);
    if (messenger.retrieve(message.getId()) == null) {
      throw new IllegalStateException("testRetrieve(): retrieve failure");
    }
    messenger.delete(message.getId());
  }

  public void testListTopic(Message message) {
    int nrBeforeTopic = messenger.listTopics().length;
    System.out.println("topics: " + nrBeforeTopic);
    messenger.store(message);
    int nrAfterTopic = messenger.listTopics().length;
    System.out.println("topics: " + nrAfterTopic);
    if (nrAfterTopic != (nrBeforeTopic + 1)) {
      throw new IllegalStateException("testListTopic: listtopic fail");
    }
    int nrBeforeSubscribe = messenger.listTopics(message.getUsername()).length;
    messenger.subscribe(message.getUsername(), message.getTopic());
    int nrAfterSubscribe = messenger.listTopics(message.getUsername()).length;
    if (nrAfterSubscribe != (nrBeforeSubscribe + 1)) {
      throw new IllegalStateException("testListTopic: listtopic(user) fail");
    }
    messenger.unsubscribe(message.getUsername(), message.getTopic());
    messenger.delete(message.getId());
  }

  public void testListUser(Message message) {
    int nrBeforeUser = messenger.listUsers().length;
    messenger.store(message);
    int nrAfterUser = messenger.listUsers().length;
    if (nrAfterUser != (nrBeforeUser + 1)) {
      throw new IllegalStateException("testListUser: listuser fail");
    }
    messenger.delete(message.getId());

  }

  // ----------------------------------------------------------
  public void testTopicWildcards() {
    Topic data = new Topic("/abc/a");
    Topic pattern = new Topic("/abc*");

    System.out.println("testing topic wildcards...");
    System.out.println("data: " + data);
    System.out.println("  wildcard: " + data.getWildcard());
    System.out.println("  value:    " + data.getValue());

    System.out.println("pattern: " + pattern);
    System.out.println("  wildcard: " + pattern.getWildcard());
    System.out.println("  value:    " + pattern.getValue());

    System.out.println("match(" + pattern + "," + data + ") = " + Topic.match(pattern, data));
    System.out.println("match(" + data + "," + pattern + ") = " + Topic.match(data, pattern));
    System.out.println("test done");
    System.out.println();
  }

  // ----------------------------------------------------------
  // ----------------------------------------------------------
  public static void test(Messenger messenger) {
    LogicTest test = new LogicTest(messenger);

    test.testTopicWildcards();

    Message msg = Util.constructRandomMessage(Util.constructRandomUsername(), Util.constructRandomTopic(), 1024);

    System.out.println("testing logic (example)...");
    test.testStoreAndDelete(msg);
    test.testSubscribeUnsubscribe(msg);
    test.testRetrieve(msg);
    Message msg2 = Util.constructRandomMessage(Util.constructRandomUsername(), Util.constructRandomTopic(), 1024);
    test.testListTopic(msg2);
    test.testListUser(msg);
    System.out.println("test done");
  }
}
