package sonia.app.bbb2influxdb.config;

//~--- JDK imports ------------------------------------------------------------
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>your name
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class Configuration
{
  @Getter
  @Setter
  private boolean clearStatisticsEnabled;
  
  @Getter
  @Setter
  private String clearStatisticsCron;
  
  @Getter
  @Setter
  private String timezone;
  
  @Getter
  @Setter
  private String configName;

  @Getter
  @Setter
  private long interval;

  @Getter
  @Setter
  private String dbUrl;

  @Getter
  @Setter
  private String dbUser;

  @Getter
  @Setter
  private String dbPassword;
  
  @Getter
  @Setter
  private int consolePort;
  
  @Getter
  @Setter
  @XmlElementWrapper(name="hosts")
  @XmlElement( name = "host" ) 
  List<Host> hosts;
}
