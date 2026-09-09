package se.umu.cs.ads.a1.patterns.factory;

public class GRPCSimpleMessenger extends AbstractSimpleMessenger
{
  //----------------------------------------------------------
  public GRPCSimpleMessenger (String host, int port)
  {
    super(host,port);
  }

  //----------------------------------------------------------
  @Override
  public void hey ()
  {
    // TODO: make a gRPC invocation
  }
}
