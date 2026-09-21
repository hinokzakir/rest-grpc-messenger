package se.umu.cs.ads.a1.clients;

import se.umu.cs.ads.a1.interfaces.Messenger;
import se.umu.cs.ads.a1.types.Content;
import se.umu.cs.ads.a1.types.Data;
import se.umu.cs.ads.a1.types.Message;
import se.umu.cs.ads.a1.types.MessageId;
import se.umu.cs.ads.a1.types.Topic;
import se.umu.cs.ads.a1.types.Username;
import se.umu.cs.ads.a1.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Locale;

public class PerformanceTest
{
  private final Messenger messenger;

  //----------------------------------------------------------
  public PerformanceTest (Messenger messenger)
  {
    this.messenger = messenger;
  }

  //----------------------------------------------------------
  // example performance test
  public void testMessageRetrieval (Username username, int nrMessages, int payloadSize)
  {
    runBenchmark(username, nrMessages, payloadSize, 1, "performance_results.csv");
  }

  // automated performance benchmark suite, outputs to CSV
  public void runBenchmarkSuite (Username username, String csvFilePath)
  {
    int[] messageCounts = {10, 50, 100, 250, 500};
    int[] payloadSizes = {64, 1024, 10240, 102400}; // 64B, 1KB, 10KB, 100KB
    int iterations = 5; // repeated runs for box plot statistics

    System.out.println("Starting Automated Retrieval Performance Test Suite");
    System.out.println("Messenger Implementation: " + messenger.getClass().getName());
    System.out.println("CSV Export File:          " + csvFilePath);
    System.out.println("Iterations per Config:   " + iterations);

    File file = new File(csvFilePath);
    boolean writeHeader = !file.exists();

    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
      if (writeHeader) {
        writer.println("messenger_type,mode,nr_messages,payload_size_bytes,total_bytes,iteration,duration_ms,avg_latency_ms,throughput_msg_sec,bandwidth_mb_sec");
        writer.flush();
      }

      String messengerType = messenger.getClass().getSimpleName();

      // scaling message count, fixed 1KB payload
      System.out.println("Scaling Message Count");
      for (int count : messageCounts) {
        runConfig(writer, messengerType, username, count, 1024, iterations);
      }

      // scaling message size (fixed 100 messages)
      System.out.println("Scaling Message Payload Size");
      for (int size : payloadSizes) {
        if (size != 1024){ // skip 1024 duplicate
          runConfig(writer, messengerType, username, 100, size, iterations);
        }
      }

      System.out.println("Benchmark completed! CSV exported to: " + csvFilePath);

    } catch (IOException e) {
      System.err.println("Error writing performance results to CSV: " + e.getMessage());
      e.printStackTrace();
    }
  }

  //----------------------------------------------------------
  public void runBenchmark (Username username, int nrMessages, int payloadSize, int iterations, String csvFilePath)
  {
    File file = new File(csvFilePath);
    boolean writeHeader = !file.exists();

    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
      if (writeHeader) {
        writer.println("messenger_type,mode,nr_messages,payload_size_bytes,total_bytes,iteration,duration_ms,avg_latency_ms,throughput_msg_sec,bandwidth_mb_sec");
        writer.flush();
      }

      runConfig(writer, messenger.getClass().getSimpleName(), username, nrMessages, payloadSize, iterations);
    } catch (IOException e) {
      System.err.println("Error writing performance results: " + e.getMessage());
    }
  }

  //----------------------------------------------------------
  private void runConfig (PrintWriter writer, String messengerType, Username username, int nrMessages, int payloadSize, int iterations)
  {
    Topic topic = new Topic("/test/performance");

    for (int iter = 1; iter <= iterations; iter++) {
      // setup test data with retry resilience
      MessageId[] messageIds = new MessageId[0];
      for (int attempt = 1; attempt <= 3; attempt++) {
        messenger.delete(messenger.listMessages(topic));
        Content content = Content.EMPTY;
        Data data = Util.constructRandomData(payloadSize);
        Message[] messages = Message.construct(username, topic, content, data, nrMessages);
        messenger.store(messages);

        messageIds = messenger.listMessages(topic);
        if (messageIds.length == nrMessages) {
          break;
        }
        try { Thread.sleep(100); } catch (Exception ignored) {}
      }

      if (messageIds.length != nrMessages) {
        messenger.delete(messenger.listMessages(topic));
        throw new IllegalStateException("testMessageRetrieval(): setup failure, expected " + nrMessages + " but got " + messageIds.length);
      }

      long totalBytes = (long) nrMessages * payloadSize;

      // sequential (individual) retrieval
      long t1 = System.currentTimeMillis();
      for (MessageId msgId : messageIds) {
        messenger.retrieve(msgId);
      }
      long t2 = System.currentTimeMillis();
      long seqDuration = Math.max(1, t2 - t1);
      double seqLatency = (double) seqDuration / nrMessages;
      double seqThroughput = (nrMessages * 1000.0) / seqDuration;
      double seqBandwidth = ((double) totalBytes / (1024.0 * 1024.0)) / ((double) seqDuration / 1000.0);

      writer.println(String.format(Locale.US, "%s,sequential,%d,%d,%d,%d,%d,%.4f,%.2f,%.4f",
          messengerType, nrMessages, payloadSize, totalBytes, iter,
          seqDuration, seqLatency, seqThroughput, seqBandwidth));

      // batch retrieval
      long t3 = System.currentTimeMillis();
      messenger.retrieve(messageIds);
      long t4 = System.currentTimeMillis();
      long batchDuration = Math.max(1, t4 - t3);
      double batchLatency = (double) batchDuration / nrMessages;
      double batchThroughput = (nrMessages * 1000.0) / batchDuration;
      double batchBandwidth = ((double) totalBytes / (1024.0 * 1024.0)) / ((double) batchDuration / 1000.0);

      writer.println(String.format(Locale.US, "%s,batch,%d,%d,%d,%d,%d,%.4f,%.2f,%.4f",
          messengerType, nrMessages, payloadSize, totalBytes, iter,
          batchDuration, batchLatency, batchThroughput, batchBandwidth));

      writer.flush();

      System.out.printf(Locale.US, "[Iter %d/%d] Count: %4d | Size: %6d B | Seq: %4d ms (%.2f msg/s) | Batch: %4d ms (%.2f msg/s)%n",
          iter, iterations, nrMessages, payloadSize,
          seqDuration, seqThroughput, batchDuration, batchThroughput);

      // cleanup after run
      messenger.delete(messenger.listMessages(topic));
      try { Thread.sleep(50); } catch (Exception ignored) {}
    }
  }
}
