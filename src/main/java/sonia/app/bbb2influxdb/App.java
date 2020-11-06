package sonia.app.bbb2influxdb;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import javax.xml.bind.JAXB;
import lombok.Getter;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.app.bbb2influxdb.config.Configuration;
import sonia.app.bbb2influxdb.cronjob.ClearStatisticsJob;
import sonia.commons.bigbluebutton.client.GlobalStatistics;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class App
{
  private static final String CONFIGURATION = "config.xml";

  private static final String SAVE_STATE = "save.xml";

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
    "YYYYMMddHHmmss");

  final static Logger LOGGER = LoggerFactory.getLogger(App.class.
    getName());

  @Getter
  private static Configuration config;

  public static void buildInfo(PrintStream out)
  {
    BuildProperties build = BuildProperties.getInstance();
    out.println("Project Name    : " + build.getProjectName());
    out.println("Project Version : " + build.getProjectVersion());
    out.println("Build Timestamp : " + build.getTimestamp());
    out.flush();
  }

  public static void readConfiguration()
  {
    LOGGER.info("reading configuration file config.xml 2");

    try
    {
      Configuration c = null;
      File configFile = new File(CONFIGURATION);

      LOGGER.info("Config file: {}", configFile.getAbsolutePath());

      if (configFile.exists() && configFile.canRead())
      {
        c = JAXB.unmarshal(new FileReader(configFile), Configuration.class);

        LOGGER.debug("new config <{}>", c);

        if (c != null)
        {
          LOGGER.info("setting config");
          config = c;
        }
      }
      else
      {
        LOGGER.info("Can NOT read config file");
      }

      if (config == null)
      {
        LOGGER.error("config file NOT found");
        System.exit(2);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Configuratione file config.xml not found ", e);
      System.exit(2);
    }
  }

  public static void saveState()
  {
    try
    {
      JAXB.marshal(GlobalStatistics.getInstance(),
        new FileWriter(SAVE_STATE));
      // Backup of save state
      JAXB.marshal(GlobalStatistics.getInstance(),
        new FileWriter(SAVE_STATE + "." + DATE_FORMAT.format(
          new java.util.Date())));
    }
    catch (IOException ex)
    {
      LOGGER.error("Can not write save state");
    }
  }

  public static void main(String[] args) throws Exception
  {
    BuildProperties build = BuildProperties.getInstance();
    LOGGER.info("Project Name    : " + build.getProjectName());
    LOGGER.info("Project Version : " + build.getProjectVersion());
    LOGGER.info("Build Timestamp : " + build.getTimestamp());

    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run()
      {
        LOGGER.info("Shutdown Hook is running !");
        saveState();
      }
    });

    readConfiguration();
    LOGGER.debug(config.toString());

    new Console(config.getConsolePort()).start();

    String timezone = config.getTimezone();

    if (timezone != null && timezone.trim().length() > 0)
    {
      TimeZone.setDefault(TimeZone.getTimeZone(timezone));
    }

    File saveFile = new File(SAVE_STATE);
    if (saveFile.exists() && saveFile.canRead())
    {
      LOGGER.info("Reading global statistics from save.xml.");
      GlobalStatistics.readSavedState(saveFile);
    }

    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    Scheduler scheduler = schedulerFactory.getScheduler();
    scheduler.start();

    if (config.isClearStatisticsEnabled())
    {
      LOGGER.debug("Setting up clear statistics cron job.");

      JobDetail job = JobBuilder.newJob(ClearStatisticsJob.class)
        .withIdentity("ClearStatisticsJob", "group1")
        .build();

      CronTrigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(CronScheduleBuilder.cronSchedule(config.
          getClearStatisticsCron()))
        .build();
      scheduler.scheduleJob(job, trigger);
    }

    TransferTask task = new TransferTask(config);
    Timer timer = new Timer();
    System.out.println("Running task every " + config.getInterval()
      + " seconds.");
    timer.scheduleAtFixedRate(task, 0, config.getInterval() * 1000);
  }
}
