package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import sonia.app.bbb2influxdb.App;

/**
 *
 * @author th
 */
public class buildinfo
{
  public static void invoke(Interpreter env, CallStack callstack)
  {
    App.buildInfo(env.getOut());
  }
}

