# rest-grpc-messenger
Message management system where (using a service interface / API) developers are able to manage messages via topic-based subscriptions.

## Useful Commands
```bash
mvn compile
mvn package
```

```bash
java -cp target/rest-grpc-messenger-1.2.6.jar se.umu.cs.ads.a1.rest.RESTServer

java -cp target/rest-grpc-messenger-1.2.6.jar se.umu.cs.ads.a1.grpc.GRPCServer
```

```bash
./t.sh se.umu.cs.ads.a1.rest.RESTMessenger -logic

./t.sh se.umu.cs.ads.a1.grpc.GRPCMessenger -logic
```


