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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class RecordingFormat
{
  @Getter
  private String type;

  @Getter
  @XmlElement(name = "url")
  private String url;

  @Getter
  private long processingTime;

  @Getter
  private long length;

  @Getter
  private long size;
}
