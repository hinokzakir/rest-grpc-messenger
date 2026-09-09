package se.umu.cs.ads.a1.clients;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;
import se.umu.cs.ads.a1.util.Util;

public class LogicTest
{
  private final Messenger messenger;


  //----------------------------------------------------------
  private LogicTest (Messenger messenger)
  {
    this.messenger = messenger;
  }

  //----------------------------------------------------------
  // example logic test
	public void testStoreAndDelete (Message message)
	{
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

  //----------------------------------------------------------
  public void testTopicWildcards ()
  {
    Topic data = new Topic("/abc/a");
    Topic pattern = new Topic("/abc*");

    System.out.println("testing topic wildcards...");
    System.out.println("data: " + data);
    System.out.println("  wildcard: " + data.getWildcard());
    System.out.println("  value:    " + data.getValue());

    System.out.println("pattern: " + pattern);
    System.out.println("  wildcard: " + pattern.getWildcard());
    System.out.println("  value:    " + pattern.getValue());

    System.out.println("match(" + pattern + "," + data + ") = " + Topic.match(pattern,data));
    System.out.println("match(" + data + "," + pattern + ") = " + Topic.match(data,pattern));
    System.out.println("test done");
    System.out.println();
  }


  //----------------------------------------------------------
  //----------------------------------------------------------
  public static void test (Messenger messenger)
  {
    LogicTest test = new LogicTest(messenger);

    test.testTopicWildcards();

    System.out.println("testing logic (example)...");
    test.testStoreAndDelete(Util.constructRandomMessage(Util.constructRandomUsername(),Util.constructRandomTopic(),1024));
    System.out.println("test done");
  }
}
