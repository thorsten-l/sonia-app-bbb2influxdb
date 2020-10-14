package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.LoggerFactory;

/**
 *
 * @author th
 */
public class exit
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(exit.class.
    getName());

  public static void invoke(Interpreter env, CallStack callstack)
  {
    env.getOut().println("Closing connection.");
    env.getOut().flush();
    try
    {
      Socket client = (Socket) env.get("thisClientSocket");
      client.close();
    }
    catch (EvalError | IOException ex)
    {
      LOGGER.error("Unable closing connection.", ex);
    }
  }
}
