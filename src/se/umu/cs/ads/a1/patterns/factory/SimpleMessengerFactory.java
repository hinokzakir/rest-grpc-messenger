package se.umu.cs.ads.a1.patterns.factory;

public interface SimpleMessengerFactory
{
  public enum SimpleMessengerType {REST, GRPC};


  //----------------------------------------------------------
  public SimpleMessenger getMessenger (SimpleMessengerType type, String host, int port);


  //----------------------------------------------------------
  //----------------------------------------------------------
  public static SimpleMessengerType parse (String type)
  {
    return type.equalsIgnoreCase("gRPC") ? SimpleMessengerType.GRPC : SimpleMessengerType.REST;
  }


  //----------------------------------------------------------
  //----------------------------------------------------------
  public static SimpleMessengerFactory loadFactory (String fqn)
  {
    try
    {
      Class<?> _class = Class.forName(fqn);
      return (SimpleMessengerFactory)_class.getDeclaredConstructor().newInstance();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new IllegalStateException("unable to instantiate messenger class '" + fqn + "'");
    }
  }
}
