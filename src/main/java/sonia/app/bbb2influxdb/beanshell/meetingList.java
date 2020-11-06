package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import com.google.common.base.Strings;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import sonia.commons.bigbluebutton.client.Attendee;
import sonia.commons.bigbluebutton.client.GlobalStatistics;
import sonia.commons.bigbluebutton.client.Meeting;

/**
 *
 * @author th
 */
public class meetingList
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(quit.class.
    getName());

  public static void invoke(Interpreter env, CallStack callstack)
  {
    LOGGER.debug("meetings list");
    HashMap<String, Meeting> meetings = GlobalStatistics.getInstance().
      getUniqueMeetings();
    HashMap<String, String> meetingsHosts = GlobalStatistics.getInstance().
      getUniqueMeetingsHosts();

    String[] ids = meetings.keySet().toArray(new String[0]);

    for (String id : ids)
    {
      Meeting meeting = meetings.get(id);

      String origin = meeting.getMetadata().getOrigin();

      if (Strings.isNullOrEmpty(origin))
      {
        origin = "Stud.IP";
      }

      env.getOut().println(id + ", " + meetingsHosts.get(id) + ", " + meeting.
        getMeetingName() + ", "
        + meeting.isRunning() + ", " + origin
        + ", " + meeting.getMetadata().getOriginServerName()
      );
    }
  }

  public static void invoke(Interpreter env, CallStack callstack,
    String nodename)
  {
    LOGGER.debug("meetings list for node = {}", nodename);

    HashMap<String, Meeting> meetings = GlobalStatistics.getInstance().
      getUniqueMeetings();
    HashMap<String, String> meetingsHosts = GlobalStatistics.getInstance().
      getUniqueMeetingsHosts();

    String[] ids = meetingsHosts.keySet().toArray(new String[0]);

    int counter = 0;
    
    for (String id : ids)
    {
      String hostname = meetingsHosts.get(id);

      if (nodename.equalsIgnoreCase(hostname))
      {
        counter++;
        Meeting meeting = meetings.get(id);

        String origin = meeting.getMetadata().getOrigin();

        if (Strings.isNullOrEmpty(origin))
        {
          origin = "Stud.IP";
        }

        env.getOut().println( 
          id + ", " 
          + meetingsHosts.get(id) 
          + ", " + meeting.getMeetingName() + ", "
          + meeting.isRunning() + ", " + origin
          + ", " + meeting.getMetadata().getOriginServerName()
        );
        
        String firstModerator = "";
        
        for( Attendee a : meeting.getAttendees())
        {
          String role = a.getRole();
          if ( role.indexOf("MODERATOR")>=0)
          {
           firstModerator = a.getFullName() + ", " + a.getClientType();
           break;
          }
        }
        
        env.getOut().println( "  - " + meeting.getCreateDate() + ", " + firstModerator );
      }
    }
    env.getOut().println( "----------\n" + counter + " meetings" );
  }

}
