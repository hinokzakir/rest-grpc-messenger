package se.umu.cs.ads.a1.patterns.factory;

public class RESTSimpleMessenger extends AbstractSimpleMessenger
{
  //----------------------------------------------------------
  public RESTSimpleMessenger (String host, int port)
  {
    super(host,port);
  }

  //----------------------------------------------------------
  @Override
  public void hey ()
  {
    // TODO: make a REST invocation
  }
}
