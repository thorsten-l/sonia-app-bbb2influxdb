package sonia.app.bbb2influxdb;

import java.io.FileReader;
import java.util.TimeZone;
import java.util.Timer;
import javax.xml.bind.JAXB;
import jdk.internal.joptsimple.internal.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.app.bbb2influxdb.config.Configuration;

/**
 *
 * @author Thosten Ludewig <t.ludewig@ostfalia.de>
 */
public class App
{

  private static final String CONFIGURATION = "config.xml";

  final static Logger LOGGER = LoggerFactory.getLogger(App.class.
    getName());

  private static Configuration config;

  public static void main(String[] args) throws Exception
  {
    try
    {
      config = JAXB.
        unmarshal(new FileReader(CONFIGURATION), Configuration.class);

      if (config == null)
      {
        System.out.println("config file NOT found");
        System.exit(2);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Configuratione file config.xml not found");
      System.exit(2);
    }

    String timezone = config.getTimezone();
    
    if ( timezone != null && timezone.trim().length() > 0)
    {
      TimeZone.setDefault(TimeZone.getTimeZone(timezone));
    }
    
    TransferTask task = new TransferTask(config);
    Timer timer = new Timer();
    System.out.println("Running task every " + config.getInterval()
      + " seconds.");
    timer.scheduleAtFixedRate(task, 0, config.getInterval() * 1000);
  }
}
