package sonia.app.bbb2influxdb.config;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class Host
{
  /**
   * Field description
   */
  @Getter
  @Setter
  private String hostname;

  /**
   * Field description
   */
  @Getter
  @Setter
  private String apiUrl;

  @Getter
  @Setter
  private String secret;  
}
