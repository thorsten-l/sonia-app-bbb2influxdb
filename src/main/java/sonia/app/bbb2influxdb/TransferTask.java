package sonia.app.bbb2influxdb;

import com.google.common.base.Strings;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TimerTask;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.app.bbb2influxdb.config.Configuration;
import sonia.app.bbb2influxdb.config.Host;
import sonia.commons.bigbluebutton.client.BbbClient;
import sonia.commons.bigbluebutton.client.BbbClientFactory;
import sonia.commons.bigbluebutton.client.GlobalStatistics;
import sonia.commons.bigbluebutton.client.Statistics;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class TransferTask extends TimerTask
{
  final static Logger LOGGER = LoggerFactory.getLogger(TransferTask.class.
    getName());

  private Configuration config;

  public TransferTask(Configuration config)
  {
    this.config = config;
  }

  @Override
  public void run()
  {
    LOGGER.info("Running transfer task.");

    String message = "";
    int numberOfMeetings = 0;
    int largestConference = 0;
    int numberOfUsers = 0;
    int numberOfAudioStreams = 0;
    int numberOfVideoStreams = 0;
    int numberOfListenOnlyStreams = 0;
    int numberOfViewerOnlyStreams = 0;

    GlobalStatistics.lock();
    GlobalStatistics.clearOrigins();

    for (Host host : config.getHosts())
    {
      if (host.isOffline() || Strings.isNullOrEmpty(host.getSecret()))
      {
        LOGGER.info("Host '{}' is marked OFFLINE or has no secret.", host.getHostname());
      }
      else
      {
        String hostname = host.getHostname();
        LOGGER.info("Getting statistics for host = {}", hostname);
        try
        {
          BbbClient client = BbbClientFactory.createClient(host.getApiUrl(),
            host.
              getSecret());

          Statistics statistics = client.getStatistics(hostname);

          numberOfMeetings += statistics.getNumberOfMeetings();
          largestConference = Math.max(largestConference, statistics.
            getLargestConference());
          numberOfUsers += statistics.getNumberOfUsers();
          numberOfAudioStreams += statistics.getNumberOfAudioStreams();
          numberOfVideoStreams += statistics.getNumberOfVideoStreams();
          numberOfListenOnlyStreams += statistics.getNumberOfListenOnlyStreams();
          numberOfViewerOnlyStreams += statistics.getNumberOfViewerOnlyStreams();

          message += "meetings,host=" + hostname + " value=" + statistics.
            getNumberOfMeetings() + "\n";
          message += "users,host=" + hostname + " value=" + statistics.
            getNumberOfUsers() + "\n";
          message += "audio,host=" + hostname + " value=" + statistics.
            getNumberOfAudioStreams() + "\n";
          message += "video,host=" + hostname + " value=" + statistics.
            getNumberOfVideoStreams() + "\n";
          message += "listenOnly,host=" + hostname + " value=" + statistics.
            getNumberOfListenOnlyStreams() + "\n";
          message += "viewerOnly,host=" + hostname + " value=" + statistics.
            getNumberOfViewerOnlyStreams() + "\n";
          message += "largestConference,host=" + hostname + " value="
            + statistics.getLargestConference() + "\n";
          message += "maxVideostreamsInSingleMeeting,host=" + hostname
            + " value=" + statistics.getMaxVideostreamsInSingleMeeting() + "\n";
          message += "uniqueUsers,host=" + hostname + " value="
            + GlobalStatistics.getNumberOfUniqueUsers(hostname) + "\n";

          HashMap<String, Long> usersPerOrigin = statistics.getUsersPerOrigin();

          if (!usersPerOrigin.isEmpty())
          {
            char seperator = ' ';
            message += "usersPerOrigin,host=" + hostname;
            for (String origin : usersPerOrigin.keySet().toArray(new String[0]))
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
    }

    if (message.length() > 0)
    {
      GlobalStatistics.computeMeetingStatistics();

      message += "meetings,host=" + config.getConfigName() + " value="
        + numberOfMeetings + "\n";
      message += "users,host=" + config.getConfigName() + " value="
        + numberOfUsers + "\n";
      message += "audio,host=" + config.getConfigName() + " value="
        + numberOfAudioStreams + "\n";
      message += "video,host=" + config.getConfigName() + " value="
        + numberOfVideoStreams + "\n";
      message += "listenOnly,host=" + config.getConfigName() + " value="
        + numberOfListenOnlyStreams + "\n";
      message += "viewerOnly,host=" + config.getConfigName() + " value="
        + numberOfViewerOnlyStreams + "\n";
      message += "largestConference,host=" + config.getConfigName() + " value="
        + largestConference + "\n";

      GlobalStatistics gs = GlobalStatistics.getInstance();

      message += "uniqueUsers,host=" + config.getConfigName() + " value="
        + GlobalStatistics.getNumberOfUniqueUsers() + "\n";
      message += "uniqueMeetings,host=" + config.getConfigName() + " value="
        + GlobalStatistics.getNumberOfUniqueMeetings() + "\n";
      message += "uniqueUsersInMeetings,host=" + config.getConfigName()
        + " value=" + GlobalStatistics.getNumberOfUniqueUsersInMeetings() + "\n";
      message += "closedMeetingsDuration,host=" + config.getConfigName()
        + " value=" + gs.getClosedMeetingsDuration() + "\n";
      message += "closedMeetingsCounter,host=" + config.getConfigName()
        + " value=" + gs.getClosedMeetingsCounter() + "\n";
      message += "closedMeetingsAverageDuration,host=" + config.getConfigName()
        + " value=" + gs.getAverageClosedMeetingsDuration() + "\n";
      message += "runningMeetingsCounter,host=" + config.getConfigName()
        + " value=" + gs.getRunningMeetingsCounter() + "\n";
      message += "maxVideostreamsInSingleMeeting,host=" + config.getConfigName()
        + " value=" + gs.getMaxVideostreamsInSingleMeeting() + "\n";

      HashMap<String, Long> usersPerOrigin = gs.getAllUsersPerOrigin();

      if (!usersPerOrigin.isEmpty())
      {
        message += "usersPerOrigin,host=" + config.getConfigName();
        char seperator = ' ';
        for (String origin : usersPerOrigin.keySet().toArray(new String[0]))
        {
          long users = usersPerOrigin.get(origin);
          message += seperator + origin + "=" + users;
          seperator = ',';
        }
        message += "\n";
      }

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

        LOGGER.debug("response content : <{}>", content.toString());
        LOGGER.info("response code : {}", connection.getResponseCode());

      }
      catch (Exception e)
      {
        LOGGER.error("Writing to influxdb", e);
      }
      finally
      {
        if (connection != null)
        {
          LOGGER.debug("--- connection disconnect ---");
          connection.disconnect();
        }
      }
    }

    GlobalStatistics.unlock();
    System.gc();
  }
}
