/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.commons.bigbluebutton.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Thosten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlRootElement( name = "response" )
@XmlAccessorType(XmlAccessType.FIELD)
@ToString(callSuper=true)
public class MeetingResponse extends Meeting
{
  public boolean found()
  {
    return "SUCCESS".equalsIgnoreCase(returnCode);
  }
  
  @XmlElement( name = "returncode" )
  @Getter
  private String returnCode;
  
  @Getter
  private String messageKey;

  @Getter
  private String message;
}
