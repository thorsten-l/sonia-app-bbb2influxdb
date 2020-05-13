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
public class MeetingMetadata
{

  @XmlElement(name = "bbb-context")
  @Getter
  private String context;

  @XmlElement(name = "bbb-origin")
  @Getter
  private String origin;

  @XmlElement(name = "bbb-origin-version")
  @Getter
  private String originVersion;

  @XmlElement(name = "bbb-origin-server-name")
  @Getter
  private String originServerName;

  @XmlElement(name = "bbb-origin-tag")
  @Getter
  private String originTag;

  @XmlElement(name = "bbb-origin-server-common-name")
  @Getter
  private String originServerCommonName;

  @XmlElement(name = "bbb-recording-name")
  @Getter
  private String recordingName;

  @XmlElement(name = "bbb-recording-tags")
  @Getter
  private String recordingTags;

  @XmlElement(name = "bbb-recording-description")
  @Getter
  private String recordingDescription;

}
