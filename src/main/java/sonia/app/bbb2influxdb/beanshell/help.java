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
    + "buildInfo();     : show current build info'\n"
    + "quit();          : closing connection\n"
    + "reloadconfig();  : reload configuration from config.xml file'\n"
    + "shutdown();      : shutdown the application - same as 'System.exit(0);'\n"
    + "savestate();     : saving current global statistics to save.xml'\n"
    );
  }
}
