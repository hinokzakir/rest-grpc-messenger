package se.umu.cs.ads.a1.patterns.factory;

public class RESTGRPCSimpleMessengerFactory
  implements SimpleMessengerFactory
{
  public static RESTGRPCSimpleMessengerFactory SINGLETON = new RESTGRPCSimpleMessengerFactory();


  //----------------------------------------------------------
  //----------------------------------------------------------
  @Override
  public SimpleMessenger getMessenger (SimpleMessengerType type, String host, int port)
  {
    switch (type)
    {
      case REST : return new RESTSimpleMessenger(host,port);
      case GRPC : return new GRPCSimpleMessenger(host,port);
      default :
        throw new IllegalArgumentException("unrecognized messenger type: " + type);
    }
  }


  //----------------------------------------------------------
  //----------------------------------------------------------
  public static SimpleMessenger getInstance (SimpleMessengerType type, String host, int port)
  {
    return SINGLETON.getMessenger(type,host,port);
  }
}
