package sonia.commons.bigbluebutton.client;

import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.app.bbb2influxdb.TransferTask;

/**
 *
 * @author th
 */
@XmlRootElement
@ToString
public class Statistics
{
  final static Logger LOGGER = LoggerFactory.getLogger(Statistics.class.
    getName());

  private static final HashMap<String, String> uniqueUsers = new HashMap();

  private static final HashMap<String, HashMap<String, String>> uniqueUsersPerHost = new HashMap();

  private static final HashMap<String, String> uniqueMeetings = new HashMap();

  Statistics(List<Meeting> meetings, String hostname)
  {
    HashMap<String, String> uniqueUsersForHost = uniqueUsersPerHost.
      get(hostname);
    if (uniqueUsersForHost == null)
    {
      uniqueUsersForHost = new HashMap<String, String>();
      uniqueUsersPerHost.put(hostname, uniqueUsersForHost);
    }

    if (meetings != null)
    {
      numberOfMeetings = meetings.size();

      for (Meeting meeting : meetings)
      {
        uniqueMeetings.put(hostname + "," + meeting.getMeetingID(), meeting.
          getMeetingName());

        int participantCount = meeting.getParticipantCount();
        numberOfUsers += participantCount;
        largestConference = Math.max(largestConference, participantCount);
        for (Attendee attendee : meeting.getAttendees())
        {
          if (attendee.hasVideo())
          {
            numberOfVideoStreams += 1;
          }
          else if (attendee.hasJoinedVoice())
          {
            numberOfAudioStreams += 1;
          }
          else if (attendee.isListeningOnly())
          {
            numberOfListenOnlyStreams += 1;
          }
          else
          {
            numberOfViewerOnlyStreams += 1;
          }

          uniqueUsers.put(attendee.getFullName().toLowerCase(), attendee.
            getClientType());
          uniqueUsersForHost.put(attendee.getFullName().toLowerCase(), attendee.
            getClientType());
        }
      }
    }
    else
    {
      LOGGER.error("Got zero meetings for host=" + hostname);
    }
  }

  public static int getNumberOfUniqueUsers()
  {
    return uniqueUsers.size();
  }

  public static int getNumberOfUniqueUsers(String hostname)
  {
    int numberOfUniqueUsers = 0;

    HashMap<String, String> uniqueUsersForHost = uniqueUsersPerHost.
      get(hostname);

    if (uniqueUsersForHost != null)
    {
      numberOfUniqueUsers = uniqueUsersForHost.size();
    }

    return numberOfUniqueUsers;
  }

  public static int getNumberOfUniqueMeetings()
  {
    return uniqueMeetings.size();
  }

  public static void clear()
  {
    uniqueUsers.clear();
    uniqueMeetings.clear();
  }

  @Getter
  private int numberOfMeetings;

  @Getter
  private int largestConference;

  @Getter
  private int numberOfUsers;

  @Getter
  private int numberOfAudioStreams;

  @Getter
  private int numberOfVideoStreams;

  @Getter
  private int numberOfListenOnlyStreams;

  @Getter
  private int numberOfViewerOnlyStreams;

}
