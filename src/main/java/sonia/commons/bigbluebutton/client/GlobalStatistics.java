package sonia.commons.bigbluebutton.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class GlobalStatistics implements Serializable
{
  private static final long serialVersionUID = 38315001L;

  final static Logger LOGGER = LoggerFactory.getLogger(GlobalStatistics.class.
    getName());

  private static GlobalStatistics GS = new GlobalStatistics();

  private GlobalStatistics()
  {
    clearStatisticsLock = new ReentrantLock();
    statisticsCleared = false;
    statisticsClearedTimestamp = 0l;
  }

  public static GlobalStatistics getInstance()
  {
    return GS;
  }

  public static void readSavedState(File saveFile)
  {
    GS = JAXB.unmarshal(saveFile, GlobalStatistics.class);
    LOGGER.info("read saved state done.");
  }

  public static void initializePerHost(String hostname)
  {
    HashSet<String> uniqueUsersForHost = GS.uniqueUsersPerHost.
      get(hostname);

    if (uniqueUsersForHost == null)
    {
      uniqueUsersForHost = new HashSet<String>();
      GS.uniqueUsersPerHost.put(hostname, uniqueUsersForHost);
    }
  }

  public static void storeUniqueMeeting(Meeting meeting, String hostname )
  {
    GS.uniqueMeetings.put(meeting.getInternalMeetingID(), meeting);
    GS.uniqueMeetingsHosts.put(meeting.getInternalMeetingID(), hostname);
    GS.currentMeetings.put(meeting.getInternalMeetingID(), meeting);
    GS.maxVideostreamsInSingleMeeting = Math.max(
      GS.maxVideostreamsInSingleMeeting, meeting.getVideoCount());
  }

  public static void addAllUsersPerOrigin(String origin, Meeting meeting)
  {
    if (!GS.allUsersPerOrigin.containsKey(origin))
    {
      GS.allUsersPerOrigin.put(origin, 0l);
    }
    long originCounter = GS.allUsersPerOrigin.get(origin);
    originCounter += meeting.getParticipantCount();
    GS.allUsersPerOrigin.put(origin, originCounter);
  }

  public static void addAttendee(Attendee attendee, Meeting meeting,
    String hostname)
  {
    GS.uniqueUsers.add(attendee.getUserID());
    HashSet<String> uniqueUsersForHost = GS.uniqueUsersPerHost.
      get(hostname);
    uniqueUsersForHost.add(attendee.getUserID());
    GS.uniqueUsersInMeetings.add(meeting.getInternalMeetingID() + "/"
      + attendee.getUserID());
  }

  public static int getNumberOfUniqueUsers()
  {
    return GS.uniqueUsers.size();
  }

  public static int getNumberOfUniqueUsersInMeetings()
  {
    return GS.uniqueUsersInMeetings.size();
  }

  public static int getNumberOfUniqueUsers(String hostname)
  {
    int numberOfUniqueUsers = 0;

    HashSet<String> uniqueUsersForHost = GS.uniqueUsersPerHost.
      get(hostname);

    if (uniqueUsersForHost != null)
    {
      numberOfUniqueUsers = uniqueUsersForHost.size();
    }

    return numberOfUniqueUsers;
  }

  public static int getNumberOfUniqueMeetings()
  {
    return GS.uniqueMeetings.size();
  }

  public static void computeMeetingStatistics()
  {
    HashMap<String, Meeting> meetingsMap = new HashMap();
    meetingsMap.putAll(GS.uniqueMeetings);

    String[] keys = GS.currentMeetings.keySet().toArray(new String[0]);

    for (String key : keys)
    {
      meetingsMap.remove(key);
    }

    keys = meetingsMap.keySet().toArray(new String[0]);

    for (String key : keys)
    {
      Meeting meeting = GS.uniqueMeetings.get(key);
      if (meeting.isRunning())
      {
        meeting.setRunning(false);
        meeting.setEndTime(System.currentTimeMillis());
      }
    }

    GS.closedMeetingsDuration = 0;
    GS.closedMeetingsCounter = 0;
    GS.runningMeetingsCounter = 0;
    long avgDuration = 0;
    long avgCounter = 0;

    keys = GS.uniqueMeetings.keySet().toArray(new String[0]);

    for (String key : keys)
    {
      Meeting meeting = GS.uniqueMeetings.get(key);

      long d = Math.
        max((meeting.getEndTime() - meeting.getStartTime()) / 60000l, 0l);

      if (meeting.isRunning())
      {
        GS.runningMeetingsCounter++;
      }
      else
      {
        if (meeting.getEndTime() == 0)
        {
          meeting.setEndTime(System.currentTimeMillis());
          d = Math.max((meeting.getEndTime() - meeting.getStartTime()) / 60000l,
            0l);
        }

        if (GS.statisticsCleared)
        {
          meeting.setInvalid(true);
        }

        if (meeting.isInvalid())
        {
          d = 0l;
        }

        if (d < 1440)
        {
          GS.closedMeetingsDuration += d;
          GS.closedMeetingsCounter++;
        }
      }

      if (d >= 5l && d < 1440l) // Ignor meetings with duration less than 5min
      {
        avgDuration += d;
        avgCounter++;
      }
    }

    if (avgCounter > 0)
    {
      GS.averageClosedMeetingsDuration = avgDuration / avgCounter;
    }

    GS.statisticsCleared = false;
  }

  public static void clearAndLog(String currentDate)
  {
    GS.statisticsClearedTimestamp = System.currentTimeMillis();
    try
    {
      try (PrintWriter writer = new PrintWriter("unique-meetings-" + currentDate
        + ".xml"))
      {
        String[] keys = GS.uniqueMeetings.keySet().toArray(new String[0]);

        Meeting[] meetings = new Meeting[GS.uniqueMeetings.size()];

        int i = 0;
        for (String key : keys)
        {
          meetings[i++] = GS.uniqueMeetings.get(key);
        }

        JAXB.marshal(meetings, writer);
      }
    }
    catch (FileNotFoundException ex)
    {
      LOGGER.error("Can't write unique meeting logfile. ", ex);
    }

    GS.uniqueUsers.clear();
    GS.uniqueMeetings.clear();
    GS.uniqueMeetingsHosts.clear();
    GS.allUsersPerOrigin.clear();
    GS.uniqueUsersInMeetings.clear();
    GS.averageClosedMeetingsDuration = 0;
    GS.maxVideostreamsInSingleMeeting = 0;
    GS.statisticsCleared = true;
  }

  public static void clearOrigins()
  {
    GS.allUsersPerOrigin.clear();
    GS.allUsersPerOrigin.put("unknown", 0l);
    GS.allUsersPerOrigin.put("moodle", 0l);
    GS.allUsersPerOrigin.put("greenlight", 0l);
    GS.currentMeetings.clear();
  }

  public static void lock()
  {
    GS.clearStatisticsLock.lock();
  }

  public static void unlock()
  {
    GS.clearStatisticsLock.unlock();
  }

  private HashSet<String> uniqueUsers = new HashSet();

  private HashSet<String> uniqueUsersInMeetings = new HashSet();

  private HashMap<String, HashSet<String>> uniqueUsersPerHost = new HashMap();

  @Getter
  private HashMap<String, Meeting> uniqueMeetings = new HashMap();
  
  @Getter
  private HashMap<String, String> uniqueMeetingsHosts = new HashMap();

  private HashMap<String, Meeting> currentMeetings = new HashMap();

  @Getter
  private HashMap<String, Long> allUsersPerOrigin = new HashMap();

  private transient Lock clearStatisticsLock;

  private boolean statisticsCleared;

  private long statisticsClearedTimestamp;

  @Getter
  private long closedMeetingsDuration;

  @Getter
  private long averageClosedMeetingsDuration;

  @Getter
  private int closedMeetingsCounter;

  @Getter
  private int runningMeetingsCounter;

  @Getter
  private int maxVideostreamsInSingleMeeting;
}
