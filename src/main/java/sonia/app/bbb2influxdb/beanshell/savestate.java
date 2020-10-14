package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import sonia.app.bbb2influxdb.App;

/**
 *
 * @author th
 */
public class savestate
{
  public static void invoke(Interpreter env, CallStack callstack)
  {
    env.getOut().println("save current golbal statistics to save.xml");
    App.saveState();
  }
}
