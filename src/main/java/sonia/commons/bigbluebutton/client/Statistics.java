package sonia.commons.bigbluebutton.client;

import java.util.HashMap;
import java.util.List;
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

  Statistics(List<Meeting> meetings, String hostname)
  {
 
    GlobalStatistics.initializePerHost(hostname);
    
    usersPerOrigin.put("unknown", 0l);
    usersPerOrigin.put("moodle", 0l);
    usersPerOrigin.put("greenlight", 0l);
    
    if (meetings != null)
    {
      numberOfMeetings = meetings.size();

      for (Meeting meeting : meetings)
      {
        GlobalStatistics.storeUniqueMeeting(meeting,hostname);
        maxVideostreamsInSingleMeeting = Math.max(maxVideostreamsInSingleMeeting, meeting.getVideoCount());
        
        // System.out.println( hostname + ":" + meeting );
        
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

        GlobalStatistics.addAllUsersPerOrigin( origin, meeting );
        
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
          
          GlobalStatistics.addAttendee( attendee, meeting, hostname );
        }
      }
    }
    else
    {
      LOGGER.error("Got zero meetings for host=" + hostname);
    }
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

  @Getter
  private int maxVideostreamsInSingleMeeting;
}
