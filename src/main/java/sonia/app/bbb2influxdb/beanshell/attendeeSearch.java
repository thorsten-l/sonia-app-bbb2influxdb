package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import java.util.HashMap;
import java.util.List;
import org.slf4j.LoggerFactory;
import sonia.commons.bigbluebutton.client.Attendee;
import sonia.commons.bigbluebutton.client.GlobalStatistics;
import sonia.commons.bigbluebutton.client.Meeting;

/**
 *
 * @author th
 */
public class attendeeSearch
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    attendeeSearch.class.
      getName());

  public static void invoke(Interpreter env, CallStack callstack,
    String search)
  {
    LOGGER.debug("meetings search for attendee name = {}", search);

    if (search != null)
    {
      int counter = 0;

      HashMap<String, Meeting> meetings = GlobalStatistics.getInstance().
        getUniqueMeetings();

      String[] ids = meetings.keySet().toArray(new String[0]);

      search = search.toLowerCase().trim();

      for (String id : ids)
      {
        List<Attendee> attendees = meetings.get(id).getAttendees();

        if (attendees != null && attendees.size() > 0)
        {
          boolean found = false;

          for (Attendee a : attendees)
          {
            if (a.getFullName().toLowerCase().trim().contains(search))
            {
              found = true;
              break;
            }
          }

          if (found)
          {
            counter++;
            meetingList.printMeeting(env.getOut(), id);
            for (Attendee a : attendees)
            {
              if (a.getFullName().toLowerCase().trim().contains(search))
              {
                env.getOut().println("    + " + a.getFullName());
              }
            }

          }
        }
      }

      env.getOut().println("----------\n" + counter + " meetings");
    }
  }

}
