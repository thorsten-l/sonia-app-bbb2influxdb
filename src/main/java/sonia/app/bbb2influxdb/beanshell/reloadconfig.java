package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import sonia.app.bbb2influxdb.App;

/**
 *
 * @author th
 */
public class reloadconfig
{
  public static void invoke(Interpreter env, CallStack callstack)
  {
    env.getOut().println("reloading configuration file");
    App.readConfiguration();
  }
}
