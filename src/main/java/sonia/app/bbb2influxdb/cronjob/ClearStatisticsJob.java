package sonia.app.bbb2influxdb.cronjob;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.commons.bigbluebutton.client.GlobalStatistics;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class ClearStatisticsJob implements Job
{
  private final static Logger LOGGER = LoggerFactory.getLogger(ClearStatisticsJob.class.getName());
  private final static SimpleDateFormat FORMAT = new SimpleDateFormat(
    "YYYYMMdd");
  
  @Override
  public void execute(JobExecutionContext jec) throws JobExecutionException
  {
    LOGGER.info("Clear statistic data");
    GlobalStatistics.clearAndLog(FORMAT.format(new Date()));
    GlobalStatistics.clearOrigins();
  }
}
