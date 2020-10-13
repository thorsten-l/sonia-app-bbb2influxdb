/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.app.bbb2influxdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.LoggerFactory;
import static sonia.app.bbb2influxdb.App.LOGGER;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class Console implements Runnable
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Console.class.
    getName());

  private final int port;

  private ServerSocket serverSocket;

  private boolean started = false;

  public Console(int port) throws IOException
  {
    if (port <= 0)
    {
      this.port = 8642;
    }
    else
    {
      this.port = port;
    }
  }

  public synchronized void start()
  {
    if (!started)
    {
      started = true;
      Thread consoleServer = new Thread(this, "ConsoleServer");
      consoleServer.setDaemon(false);
      consoleServer.start();
    }
  }

  @Override
  public void run()
  {
    try
    {
      serverSocket = new ServerSocket(port, 0, InetAddress.
        getLoopbackAddress());
      LOGGER.info("Console started on port={}", port);

      while (true)
      {
        Socket client = serverSocket.accept();
        Thread consoleClient = new Thread(new ConsoleClient(client),
          "ConsoleClient");
        consoleClient.setDaemon(false);
        consoleClient.start();
      }
    }
    catch (IOException ex)
    {
      LOGGER.error("Console communication error ", ex);
    }
  }
}
