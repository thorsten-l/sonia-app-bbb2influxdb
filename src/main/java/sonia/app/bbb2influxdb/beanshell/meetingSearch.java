package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import com.google.common.base.Strings;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import sonia.commons.bigbluebutton.client.GlobalStatistics;
import sonia.commons.bigbluebutton.client.Meeting;

/**
 *
 * @author th
 */
public class meetingSearch
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    meetingSearch.class.
      getName());

  public static void invoke(Interpreter env, CallStack callstack,
    String search)
  {
    LOGGER.debug("meetings serach for meeting name = {}", search);

    if (search != null)
    {
      int counter = 0;

      HashMap<String, Meeting> meetings = GlobalStatistics.getInstance().
        getUniqueMeetings();

      String[] ids = meetings.keySet().toArray(new String[0]);

      for (String id : ids)
      {
        Meeting meeting = meetings.get(id);
        String name = meeting.getMeetingName();
        String context = (meeting.getMetadata() != null && !Strings.
          isNullOrEmpty(
            meeting.getMetadata().getContext()) ? meeting.getMetadata().
          getContext() : "");

        name = name.toLowerCase().trim();
        context = context.toLowerCase().trim();
        search = search.toLowerCase().trim();

        if (name.contains(search) || context.contains(search))
        {
          counter++;
          meetingList.printMeeting(env.getOut(), id);
        }
      }

      env.getOut().println("----------\n" + counter + " meetings");
    }
  }

}
