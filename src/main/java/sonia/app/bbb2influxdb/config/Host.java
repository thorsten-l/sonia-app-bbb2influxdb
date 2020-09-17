/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.app.bbb2influxdb.config;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>th
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
