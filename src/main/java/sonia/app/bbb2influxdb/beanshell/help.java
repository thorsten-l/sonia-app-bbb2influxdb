package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;

/**
 *
 * @author th
 */
public class help
{
  public static void invoke(Interpreter env, CallStack callstack)
  {
    env.getOut().println("\n--- Additional commands ---\n"
    + "buildinfo();     : show current build info'\n"
    + "quit();          : closing connection\n"
    + "reloadconfig();  : reload configuration from config.xml file'\n"
    + "shutdown();      : shutdown the application - same as 'System.exit(0);'\n"
    + "savestate();     : saving current global statistics to save.xml'\n\n"
    + "nodeInfo(String name);  : info for one node'\n"
    + "nodeList();             : list all nodes'\n"
    + "nodeSetOffline(String name, boolean offline);\n"
    + "                        : set node offline or online'\n\n"
    + "meetingList();                : list all meetings'\n"
    + "meetingList(String nodename); : list all meetings of the specified node'\n"
    + "meetingInfo(String id);       : info for one meeting'\n"
    + "meetingSearch(String search); : search for meeting name and context name'\n\n"
    + "attendeeSearch(String search); : search for attendee in meetings'\n"
    );
  }
}
