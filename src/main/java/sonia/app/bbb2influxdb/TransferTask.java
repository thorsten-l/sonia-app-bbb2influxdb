package sonia.app.bbb2influxdb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.app.bbb2influxdb.config.Configuration;
import sonia.app.bbb2influxdb.config.Host;
import sonia.commons.bigbluebutton.client.BbbClient;
import sonia.commons.bigbluebutton.client.BbbClientFactory;
import sonia.commons.bigbluebutton.client.Meeting;
import sonia.commons.bigbluebutton.client.Statistics;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>th
 */
public class TransferTask extends TimerTask
{
  final static Logger LOGGER = LoggerFactory.getLogger(TransferTask.class.
    getName());

  private final static SimpleDateFormat FORMAT = new SimpleDateFormat(
    "YYYYMMdd");

  private static String currentDate;

  private Configuration config;

  static
  {
    currentDate = FORMAT.format(new Date());
    System.out.println(currentDate);
  }

  public TransferTask(Configuration config)
  {
    this.config = config;
  }

  @Override
  public void run()
  {
    System.out.println("\nRunning transfer task (" + new Date() + ")");
    String now = FORMAT.format(new Date());

    if (!currentDate.equalsIgnoreCase(now))
    {
      System.out.println("*** new day - clearing users hashset");
      Statistics.clear(currentDate);
      currentDate = new String(now);
    }

    String message = "";
    int numberOfMeetings = 0;
    int largestConference = 0;
    int numberOfUsers = 0;
    int numberOfAudioStreams = 0;
    int numberOfVideoStreams = 0;
    int numberOfListenOnlyStreams = 0;
    int numberOfViewerOnlyStreams = 0;
    
    long meetingDuration = 0;

    Statistics.clearOrigins();
    
    for (Host host : config.getHosts())
    {
      String hostname = host.getHostname();
      System.out.println("Getting statistics for host = " + hostname);
      try
      {
        BbbClient client = BbbClientFactory.createClient(host.getApiUrl(), host.
          getSecret());

        Statistics statistics = client.getStatistics(hostname);

        numberOfMeetings += statistics.getNumberOfMeetings();
        largestConference = Math.max( largestConference, statistics.getLargestConference());
        numberOfUsers += statistics.getNumberOfUsers();
        numberOfAudioStreams += statistics.getNumberOfAudioStreams();
        numberOfVideoStreams += statistics.getNumberOfVideoStreams();
        numberOfListenOnlyStreams += statistics.getNumberOfListenOnlyStreams();
        numberOfViewerOnlyStreams += statistics.getNumberOfViewerOnlyStreams();
        
        message += "meetings,host=" + hostname + " value=" + statistics.getNumberOfMeetings() + "\n";
        message += "users,host=" + hostname + " value=" + statistics.getNumberOfUsers() + "\n";
        message += "audio,host=" + hostname + " value=" + statistics.getNumberOfAudioStreams() + "\n";
        message += "video,host=" + hostname + " value=" + statistics.getNumberOfVideoStreams() + "\n";
        message += "listenOnly,host=" + hostname + " value=" + statistics.getNumberOfListenOnlyStreams() + "\n";
        message += "viewerOnly,host=" + hostname + " value=" + statistics.getNumberOfViewerOnlyStreams() + "\n";
        message += "largestConference,host=" + hostname + " value=" + statistics.getLargestConference() + "\n";
        message += "uniqueUsers,host=" + hostname + " value=" + Statistics.getNumberOfUniqueUsers(hostname) + "\n";
                
        HashMap<String, Long> usersPerOrigin = statistics.getUsersPerOrigin();
        
        if ( !usersPerOrigin.isEmpty() )
        {
          char seperator = ' ';
          message += "usersPerOrigin,host=" + hostname;
          for( String origin : usersPerOrigin.keySet().toArray(new String[0]))
          {
            long users = usersPerOrigin.get(origin);
            message += seperator + origin + "=" + users;
            seperator = ',';
          }
          message += "\n";
        }
      }
      catch (Exception e)
      {
        LOGGER.error("Reading statistics for host=" + host.getHostname(), e);
      }
    }

    System.out.println();

    if (message.length() > 0)
    {
      message += "meetings,host=" + config.getConfigName() + " value=" + numberOfMeetings + "\n";
      message += "users,host=" + config.getConfigName() + " value=" + numberOfUsers + "\n";
      message += "audio,host=" + config.getConfigName() + " value=" + numberOfAudioStreams + "\n";
      message += "video,host=" + config.getConfigName() + " value=" + numberOfVideoStreams + "\n";
      message += "listenOnly,host=" + config.getConfigName() + " value=" + numberOfListenOnlyStreams + "\n";
      message += "viewerOnly,host=" + config.getConfigName() + " value=" + numberOfViewerOnlyStreams + "\n";
      message += "largestConference,host=" + config.getConfigName() + " value=" + largestConference + "\n";

      message += "uniqueUsers,host=" + config.getConfigName() + " value=" + Statistics.getNumberOfUniqueUsers() + "\n";
      message += "uniqueMeetings,host=" + config.getConfigName() + " value=" + Statistics.getNumberOfUniqueMeetings() + "\n";
      message += "uniqueUsersInMeetings,host=" + config.getConfigName() + " value=" + Statistics.getNumberOfUniqueUsersInMeetings() + "\n";
      
      long closedMeetingsDuration = Statistics.getClosedMeetingsDuration();
      int closedMeetingsCounter = Statistics.getClosedMeetingsCounter();
      
      message += "closedMeetingsDuration,host=" + config.getConfigName() + " value=" + closedMeetingsDuration + "\n";
      message += "closedMeetingsCounter,host=" + config.getConfigName() + " value=" + closedMeetingsCounter + "\n";
      message += "closedMeetingsAverageDuration,host=" + config.getConfigName() + " value=" + ( closedMeetingsDuration / (long)closedMeetingsCounter)+ "\n";
        
      HashMap<String, Long> usersPerOrigin = Statistics.getAllUsersPerOrigin();

      if ( !usersPerOrigin.isEmpty() )
      {
        message += "usersPerOrigin,host=" + config.getConfigName();
        char seperator = ' ';
        for( String origin : usersPerOrigin.keySet().toArray(new String[0]))
        {
          long users = usersPerOrigin.get(origin);
          message += seperator + origin + "=" + users;
          seperator = ',';
        }
        message += "\n";
      }
      
      System.out.println(message);

      HttpURLConnection connection = null;

      try
      {
        URL connectionUrl = new URL(config.getDbUrl());
        connection = (HttpURLConnection) connectionUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        String auth = config.getDbUser() + ":" + config.getDbPassword();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(
          StandardCharsets.UTF_8));
        String authHeaderValue = "Basic " + new String(encodedAuth);
        connection.setRequestProperty("Authorization", authHeaderValue);
        
        try (PrintWriter writer = new PrintWriter(connection.getOutputStream()))
        {
          writer.write(message);
        }

        StringBuilder content;

        try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream())))
        {
          String line;
          content = new StringBuilder();

          while ((line = reader.readLine()) != null)
          {
            content.append(line);
            content.append(System.lineSeparator());
          }
        }

        System.out.println(content.toString());
        System.out.println("response code : " + connection.getResponseCode());

      }
      catch (Exception e)
      {
        LOGGER.error("Writing to influxdb", e);
      }
      finally
      {
        if (connection != null)
        {
          System.out.println("--- disconnect ---");
          connection.disconnect();
        }
      }
    } 
    
      
    /* DEBUG Code */
    /*int counter = 1;
    HashMap<String,Meeting> um = Statistics.getUniqueMeetings();
    if ( um.isEmpty() )
    {
      System.out.println( "\nNo unique meetings present.");
    }
    else
    {
      System.out.println( "\nUnique meeting ids:");
      
      String[] keys = um.keySet().toArray(new String[0]);
      
      for( String key : keys )
      {
        Meeting m = um.get(key);
       
        System.out.println( "  - " + (counter) + ". " + key + " : " + m.toString() );
        
        for( String k : keys )
        {
          if ( !k.equals(key))
          {
            Meeting m2 = um.get(k);
            
            if ( m.getMeetingID().equals(m2.getMeetingID()))
            {
              System.out.println( "  +++++++ " + (counter) + ". <" + key + "> : " + m.toString() );
              System.out.println( "  +++++++ " + (counter) + ". <" + k + "> : " + m2.toString() );
            }
          }
        }
        
        counter++;
      } 
    }*/
    
    System.gc();
  }
}
