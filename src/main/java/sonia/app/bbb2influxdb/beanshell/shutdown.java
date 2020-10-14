package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;

/**
 *
 * @author th
 */
public class shutdown
{
  public static void invoke(Interpreter env, CallStack callstack)
  {
    env.getOut().println( "\nApplication shutdown.");
    System.exit(0);
  }
}

