package sonia.app.bbb2influxdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class ConsoleClient implements Runnable
{

  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    ConsoleClient.class.
      getName());

  private final Socket client;

  public ConsoleClient(Socket client)
  {
    this.client = client;
  }
  
  private void printHelp( PrintWriter writer )
  {
    writer.println("\n*** Help ***");
    writer.println("help : this help");
    writer.println("shutdown : shutdown 'bbb2influxdb' with storing current state to save.xml");
    writer.println("reload config : reloading configuration file config.xml");
    writer.println("quit : quits console connection");
  }
  

  @Override
  public void run()
  {
    LOGGER.info("Client connected to console");
    try
    {
      InputStream is = client.getInputStream();
      PrintWriter writer = new PrintWriter(client.getOutputStream());

      BuildProperties build = BuildProperties.getInstance();
      writer.println("Project Name    : " + build.getProjectName());
      writer.println("Project Version : " + build.getProjectVersion());
      writer.println("Build Timestamp : " + build.getTimestamp());
      writer.print("\ntype 'help' for help\n>");
      writer.flush();

      BufferedReader reader = new BufferedReader( new InputStreamReader(is));
      String line;
      
      while(( line = reader.readLine()) != null )
      {
        line = line.trim();
        
        if ( line.equalsIgnoreCase("help"))
        {
          printHelp(writer);
        }
        
        if ( line.equalsIgnoreCase("shutdown"))
        {
          System.exit(0);
        }
        
        if ( line.equalsIgnoreCase("reload config"))
        {
          App.readConfiguration();
        }
        
        if ( line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
        {
          break;
        }
        
        writer.print(">");
        writer.flush();
      }
      
      client.close();
    }
    catch (IOException ex)
    {
      LOGGER.error("Error closing connection", ex);
    }
  }

}
