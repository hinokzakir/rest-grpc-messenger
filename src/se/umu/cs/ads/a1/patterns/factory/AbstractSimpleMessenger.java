package se.umu.cs.ads.a1.patterns.factory;

public abstract class AbstractSimpleMessenger
  implements SimpleMessenger
{
  protected final String host;
  protected final int port;


  //----------------------------------------------------------
  public AbstractSimpleMessenger (String host, int port)
  {
    this.host = host;
    this.port = port;
  }

  //----------------------------------------------------------
  public String getHost ()
  {
    return host;
  }

  //----------------------------------------------------------
  public int getPort ()
  {
    return port;
  }
}
