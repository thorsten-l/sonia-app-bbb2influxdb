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
    env.getOut().println("Help is not ready yet.");
  }
}
