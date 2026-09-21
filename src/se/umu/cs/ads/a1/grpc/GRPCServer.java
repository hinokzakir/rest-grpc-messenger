package se.umu.cs.ads.a1.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import se.umu.cs.ads.a1.backend.InMemoryMessengerBackEnd;
import se.umu.cs.ads.a1.interfaces.Messenger;

import java.io.IOException;

public class GRPCServer {
    private final Server server;
    private final Messenger backend;

    public GRPCServer(int port) {
        this.backend = new InMemoryMessengerBackEnd();
        this.server = ServerBuilder.forPort(port)
                .maxInboundMessageSize(100 * 1024 * 1024)
                .addService(new MessengerService(this.backend))
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("GRPCServer running on port: " + server.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down GRPCServer...");
            GRPCServer.this.stop();
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        GRPCServer server = new GRPCServer(port);
        server.start();
        server.blockUntilShutdown();
    }
}
