package sonia.app.bbb2influxdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import jdk.jshell.JShell;
import jdk.jshell.Snippet.Status;
import static jdk.jshell.Snippet.Status.RECOVERABLE_DEFINED;
import static jdk.jshell.Snippet.Status.RECOVERABLE_NOT_DEFINED;
import static jdk.jshell.Snippet.Status.VALID;
import jdk.jshell.SnippetEvent;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class ConsoleClientJShell implements Runnable
{

  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    ConsoleClientJShell.class.
      getName());

  private final Socket client;

  public ConsoleClientJShell(Socket client)
  {
    this.client = client;
  }

  @Override
  public void run()
  {
    LOGGER.info("Client connected to console");
    try
    {
      InputStream is = client.getInputStream();
      PrintStream out = new PrintStream(client.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      try (JShell js = JShell.create())
      {
        do
        {
          out.print("Enter some Java code: ");
          out.flush();
          String input = reader.readLine();
          if (input == null)
          {
            break;
          }
          List<SnippetEvent> events = js.eval(input);
          for (SnippetEvent e : events)
          {
            StringBuilder sb = new StringBuilder();
            if (e.causeSnippet() == null)
            {
              switch (e.status())
              {
                case VALID:
                  sb.append("Successful ");
                  break;
                case RECOVERABLE_DEFINED:
                  sb.append("With unresolved references ");
                  break;
                case RECOVERABLE_NOT_DEFINED:
                  sb.append("Possibly reparable, failed  ");
                  break;
                case REJECTED:
                  sb.append("Failed ");
                  break;
              }
              if (e.previousStatus() == Status.NONEXISTENT)
              {
                sb.append("addition");
              }
              else
              {
                sb.append("modification");
              }
              sb.append(" of ");
              sb.append(e.snippet().source());
              out.println(sb);
              if (e.value() != null)
              {
                out.printf("Value is: %s\n", e.value());
              }
              out.flush();
            }
          }
        }
        while (true);
      }
      out.println("\nGoodbye");

      client.close();
    }
    catch (IOException ex)
    {
      LOGGER.error("Error closing connection", ex);
    }
  }

}
