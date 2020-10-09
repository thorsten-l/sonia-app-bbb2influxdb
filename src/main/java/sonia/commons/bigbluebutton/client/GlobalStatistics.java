package sonia.commons.bigbluebutton.client;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
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
public class GlobalStatistics
{
  final static Logger LOGGER = LoggerFactory.getLogger(GlobalStatistics.class.
    getName());

  private static final HashSet<String> uniqueUsers = new HashSet();

  private static final HashSet<String> uniqueUsersInMeetings = new HashSet();

  private static final HashMap<String, HashSet<String>> uniqueUsersPerHost = new HashMap();

  private static final HashMap<String, Meeting> uniqueMeetings = new HashMap();
  
  private static final HashMap<String, Meeting> currentMeetings = new HashMap();

  @Getter
  private static final HashMap<String, Long> allUsersPerOrigin = new HashMap();

  public static void initializePerHost(String hostname)
  {
    HashSet<String> uniqueUsersForHost = uniqueUsersPerHost.
      get(hostname);

    if (uniqueUsersForHost == null)
    {
      uniqueUsersForHost = new HashSet<String>();
      uniqueUsersPerHost.put(hostname, uniqueUsersForHost);
    }
  }

  public static void storeUniqueMeeting(Meeting meeting)
  {
    uniqueMeetings.put(meeting.getInternalMeetingID(), meeting);
    currentMeetings.put(meeting.getInternalMeetingID(), meeting);
  }

  public static void addAllUsersPerOrigin(String origin, Meeting meeting)
  {
    if (!allUsersPerOrigin.containsKey(origin))
    {
      allUsersPerOrigin.put(origin, 0l);
    }
    long originCounter = allUsersPerOrigin.get(origin);
    originCounter += meeting.getParticipantCount();
    allUsersPerOrigin.put(origin, originCounter);
  }

  public static void addAttendee(Attendee attendee, Meeting meeting,
    String hostname)
  {
    uniqueUsers.add(attendee.getUserID());
    HashSet<String> uniqueUsersForHost = uniqueUsersPerHost.
      get(hostname);
    uniqueUsersForHost.add(attendee.getUserID());
    uniqueUsersInMeetings.add(meeting.getInternalMeetingID() + "/"
      + attendee.getUserID());
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

  public static void computeMeetingStatistics()
  {
    HashMap<String, Meeting> meetingsMap = new HashMap();
    meetingsMap.putAll(uniqueMeetings);

    String[] keys = currentMeetings.keySet().toArray(new String[0]);
    
    for (String key : keys)
    {
      meetingsMap.remove(key);
    }
    
    keys = meetingsMap.keySet().toArray(new String[0]);
    
    for (String key : keys)
    {
      Meeting meeting = uniqueMeetings.get(key);
      if ( meeting.isRunning() )
      {
        meeting.setRunning(false);
        meeting.setEndTime(System.currentTimeMillis());
      }
    }
    
    closedMeetingsDuration = 0;
    closedMeetingsCounter = 0;
    runningMeetingsCounter = 0;
    long avgDuration = 0;
    long avgCounter = 0;

    keys = uniqueMeetings.keySet().toArray(new String[0]);

    for (String key : keys)
    {
      Meeting meeting = uniqueMeetings.get(key);

      long d = Math.max((meeting.getEndTime() - meeting.getStartTime()) / 60000l, 0l);
      
      if (meeting.isRunning())
      {
        runningMeetingsCounter++;
      }
      else
      {
        if (meeting.getEndTime() == 0)
        {
          meeting.setEndTime(System.currentTimeMillis());
          
          
          d = Math.max(( meeting.getEndTime() - meeting.getStartTime()) / 60000l, 0l);
        }
        
        if ( d < 1440 )
        {
          closedMeetingsDuration += d;
          closedMeetingsCounter++;
        }
      }

      //System.out.println((( meeting.isRunning() ) ? "T" : "F" ) + "  " + d + " : " + meeting.getMeetingName());
      //System.out.println( "   " + d + " : " + meeting.getEndTime() + " " + meeting.getStartTime());

      if (d >= 5l && d < 1440l ) // Ignor meetings with duration less than 5min
      {
        avgDuration += d;
        avgCounter++;
        //System.out.println( "   AVG " + d + " : " + avgCounter + " " + avgDuration );
      }
    }
    
    if (avgCounter > 0)
    {
      averageClosedMeetingsDuration = avgDuration / avgCounter;
    }
    
    System.out.println();
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
    currentMeetings.clear();
  }

  @Getter
  private static long closedMeetingsDuration;

  @Getter
  private static long averageClosedMeetingsDuration;

  @Getter
  private static int closedMeetingsCounter;

  @Getter
  private static int runningMeetingsCounter;

}
