package sonia.commons.bigbluebutton.client;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
public class Recording
{
  @Getter
  private String recordID;
  
  @Getter
  private String meetingID;

  @Getter
  private String internalMeetingID;

  @Getter
  private String name;
  
  @XmlElement(name = "isBreakout")
  @Getter
  private boolean breakout;
  
  @Getter
  private boolean published;
  
  @Getter
  private String state;
  
  @Getter
  private long startTime;

  @Getter
  private long endTime;
  
  @Getter
  private long participants;
  
  @Getter
  private long rawSize;
  
  @Getter
  private RecordingMetadata metadata;

  @Getter
  private long size;
  
  @XmlElementWrapper(name="playback")
  @XmlElement( name = "format" ) 
  @Getter
  private List<RecordingFormat> recordingFormats;
}
