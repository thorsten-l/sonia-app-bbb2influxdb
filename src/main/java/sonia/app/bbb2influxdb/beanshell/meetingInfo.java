package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import org.slf4j.LoggerFactory;

/**
 *
 * @author th
 */
public class meetingInfo
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(meetingInfo.class.
    getName());

  public static void invoke(Interpreter env, CallStack callstack,
    String id)
  {
    LOGGER.debug("meetings info for id = {}", id);

    meetingList.printMeeting(env.getOut(), id);
  }

}
