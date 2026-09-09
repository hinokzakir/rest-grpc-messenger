package se.umu.cs.ads.a1.patterns.factory;

import se.umu.cs.ads.a1.patterns.factory.SimpleMessengerFactory.SimpleMessengerType;

public class ExampleMain
{
  //----------------------------------------------------------
  //----------------------------------------------------------
  public static void main (String[] args)
  {
    final String host              = args.length > 0 ? args[0] : "localhost";
    final int port                 = args.length > 1 ? Integer.parseInt(args[1]) : 8000;
    final SimpleMessengerType type = args.length > 2 ? SimpleMessengerFactory.parse(args[2]) : SimpleMessengerType.REST;
    final String factoryFQN        = args.length > 3 ? args[3] : "se.umu.cs.ads.a1.patterns.factory.RESTGRPCSimpleMessengerFactory";

    // factory static method
    SimpleMessenger messenger1 = RESTGRPCSimpleMessengerFactory.getInstance(type,host,port);

    // factory instance method
    RESTGRPCSimpleMessengerFactory factory1 = new RESTGRPCSimpleMessengerFactory();
    SimpleMessenger messenger2 = factory1.getMessenger(type,host,port);

    // dynamically loaded factory
    SimpleMessengerFactory factory2 = SimpleMessengerFactory.loadFactory(factoryFQN);
    SimpleMessenger messenger3 = factory2.getMessenger(type,host,port);
  }
}
