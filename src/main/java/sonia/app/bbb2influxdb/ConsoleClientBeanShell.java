package sonia.app.bbb2influxdb;

import bsh.ConsoleInterface;
import bsh.EvalError;
import bsh.Interpreter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.Socket;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class ConsoleClientBeanShell implements Runnable
{

  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    ConsoleClientBeanShell.class.
      getName());

  private final Socket client;

  public ConsoleClientBeanShell(Socket client)
  {
    this.client = client;
  }

  @Override
  public void run()
  {
    LOGGER.info("Client connected to console");
    try
    {
      final InputStream is = client.getInputStream();
      final PrintStream out = new PrintStream(client.getOutputStream());
      final InputStreamReader reader = new InputStreamReader(is);

      ConsoleInterface console = new ConsoleInterface()
      {
        @Override
        public Reader getIn()
        {
          return reader;
        }

        @Override
        public PrintStream getOut()
        {
          return out;
        }

        @Override
        public PrintStream getErr()
        {
          return out;
        }

        @Override
        public void println(Object o)
        {
          out.println( o );
        }

        @Override
        public void print(Object o)
        {
          out.print(o);
        }

        @Override
        public void error(Object o)
        {
          out.println(o);
        }
      };
      
      out.println();
      App.buildInfo(out);
      out.println("\nhelp(); : help for additional commands.\n");
      Interpreter interpreter = new Interpreter(reader, out, out, true);
      interpreter.setConsole(console);
      interpreter.setExitOnEOF(false);
      interpreter.eval("import sonia.app.bbb2influxdb.*;");
      interpreter.eval("import sonia.app.bbb2influxdb.config.*;");
      interpreter.eval("import sonia.commons.bigbluebutton.client.*;");
      interpreter.eval("importCommands(\"sonia.app.bbb2influxdb.beanshell\");");
      interpreter.set( "out", out );
      interpreter.set( "thisClientSocket", client );
      interpreter.setShowResults(true);
      interpreter.run();

      client.close();
    }
    catch (IOException | EvalError ex)
    {
      LOGGER.error("Error closing connection", ex);
    }

    LOGGER.info( "Client connection ended" );
  }

}
