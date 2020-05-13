package sonia.commons.bigbluebutton.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Thosten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlRootElement
@ToString
public class RecordingMetadata
{
  
  /*
      <metadata>
        <bbb-origin>Greenlight</bbb-origin>
        <bbb-origin-version>2.5.2</bbb-origin-version>
        <bbb-origin-server-name>vc2.sonia.de</bbb-origin-server-name>
        <meetingName>Startraum</meetingName>
        <meetingId>4fc7adc2619ce6491c9cef427174c9bbbebfee6a</meetingId>
        <gl-listed>false</gl-listed>
        <isBreakout>false</isBreakout>
      </metadata>
  */

  @XmlElement(name = "bbb-origin")
  @Getter
  private String origin;

  @XmlElement(name = "bbb-origin-version")
  @Getter
  private String originVersion;

  @XmlElement(name = "bbb-origin-server-name")
  @Getter
  private String originServerName;

  @XmlElement(name = "meetingName")
  @Getter
  private String meetingName;

  @XmlElement(name = "meetingId")
  @Getter
  private String meetingId;

  @XmlElement(name = "gl-listed")
  @Getter
  private boolean glListed;

  @XmlElement(name = "isBreakout")
  @Getter
  private boolean breakout;
}
