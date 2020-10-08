package sonia.commons.bigbluebutton.client;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>th
 */
@XmlRootElement
@ToString
public class Statistics
{
  final static Logger LOGGER = LoggerFactory.getLogger(Statistics.class.
    getName());

  private static final HashSet<String> uniqueUsers = new HashSet();

  private static final HashSet<String> uniqueUsersInMeetings = new HashSet();

  private static final HashMap<String, HashSet<String>> uniqueUsersPerHost = new HashMap();

  @Getter
  private static final HashMap<String, Meeting> uniqueMeetings = new HashMap();

  @Getter
  private static final HashMap<String, Long> allUsersPerOrigin = new HashMap();

  Statistics(List<Meeting> meetings, String hostname)
  {
    HashSet<String> uniqueUsersForHost = uniqueUsersPerHost.
      get(hostname);

    if (uniqueUsersForHost == null)
    {
      uniqueUsersForHost = new HashSet<String>();
      uniqueUsersPerHost.put(hostname, uniqueUsersForHost);
    }

    usersPerOrigin.put("unknown", 0l);
    usersPerOrigin.put("moodle", 0l);
    usersPerOrigin.put("greenlight", 0l);

    if (meetings != null)
    {
      numberOfMeetings = meetings.size();

      for (Meeting meeting : meetings)
      {
        uniqueMeetings.put(meeting.getInternalMeetingID(), meeting);
        int participantCount = meeting.getParticipantCount();
        numberOfUsers += participantCount;
        largestConference = Math.max(largestConference, participantCount);

        String origin = "unknown";

        MeetingMetadata metaData = meeting.getMetadata();
        if (metaData != null && metaData.getOrigin() != null)
        {
          String o = metaData.getOrigin().toLowerCase().trim();
          if (o.length() > 0)
          {
            origin = o;
          }
        }

        if (!usersPerOrigin.containsKey(origin))
        {
          usersPerOrigin.put(origin, 0l);
        }

        long originCounter = usersPerOrigin.get(origin);
        originCounter += meeting.getParticipantCount();
        usersPerOrigin.put(origin, originCounter);

        if (!allUsersPerOrigin.containsKey(origin))
        {
          allUsersPerOrigin.put(origin, 0l);
        }
        originCounter = allUsersPerOrigin.get(origin);
        originCounter += meeting.getParticipantCount();
        allUsersPerOrigin.put(origin, originCounter);

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

          // String attendeeFullname = attendee.getFullName().toLowerCase().trim();
          uniqueUsers.add(attendee.getUserID());
          uniqueUsersForHost.add(attendee.getUserID());
          uniqueUsersInMeetings.add(meeting.getInternalMeetingID() + "/"
            + attendee.getUserID());
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

  public static int getNumberOfUniqueUsersInMeetings()
  {
    return uniqueUsersInMeetings.size();
  }

  public static int getNumberOfUniqueUsers(String hostname)
  {
    int numberOfUniqueUsers = 0;

    HashSet<String> uniqueUsersForHost = uniqueUsersPerHost.
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

  public static long getClosedMeetingsDuration()
  {
    long duration = 0;

    String[] keys = uniqueMeetings.keySet().toArray(new String[0]);

    for (String key : keys)
    {
      Meeting meeting = uniqueMeetings.get(key);
      if ( meeting.getEndTime() > 0 )
      {
        duration += (meeting.getEndTime() - meeting.getStartTime());
      }
    }

    return duration;
  }
  
  public static int getClosedMeetingsCounter()
  {
    int counter = 0;

    String[] keys = uniqueMeetings.keySet().toArray(new String[0]);

    for (String key : keys)
    {
      Meeting meeting = uniqueMeetings.get(key);
      if ( meeting.getEndTime() > 0 )
      {
        counter++;
      }
    }

    return counter;
  }

  public static void clear(String currentDate)
  {
    try
    {
      try (PrintWriter writer = new PrintWriter("unique-meetings-" + currentDate
        + ".xml"))
      {
        String[] keys = uniqueMeetings.keySet().toArray(new String[0]);

        Meeting[] meetings = new Meeting[uniqueMeetings.size()];

        int i = 0;
        for (String key : keys)
        {
          meetings[i++] = uniqueMeetings.get(key);
        }

        JAXB.marshal(meetings, writer);
      }
    }
    catch (FileNotFoundException ex)
    {
      LOGGER.error("Can't write unique meeting logfile. ", ex);
    }

    uniqueUsers.clear();
    uniqueMeetings.clear();
    allUsersPerOrigin.clear();
    uniqueUsersInMeetings.clear();
  }

  public static void clearOrigins()
  {
    allUsersPerOrigin.clear();
    allUsersPerOrigin.put("unknown", 0l);
    allUsersPerOrigin.put("moodle", 0l);
    allUsersPerOrigin.put("greenlight", 0l);
  }

  @Getter
  private HashMap<String, Long> usersPerOrigin = new HashMap();

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
