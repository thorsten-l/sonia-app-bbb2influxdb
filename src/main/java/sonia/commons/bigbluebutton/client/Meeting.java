package sonia.commons.bigbluebutton.client;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>Thosten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class Meeting
{
  public boolean hasUserJoined()
  {
    return this.hasUserJoined;
  }

  public boolean hasBeenForciblyEnded()
  {
    return this.hasBeenForciblyEnded;
  }

  @Getter
  private String meetingName;

  @Getter
  private String meetingID;

  @Getter
  private String internalMeetingID;

  @Getter
  private long createTime;

  @Getter
  private String createDate;

  @Getter
  private int voiceBridge;

  @Getter
  private String dialNumber;

  @ToString.Exclude
  @Getter
  private String attendeePW;

  @ToString.Exclude
  @Getter
  private String moderatorPW;

  @Getter
  @Setter
  private boolean running;

  @Getter
  private long duration;

  private boolean hasUserJoined;

  @Getter
  private boolean recording;

  private boolean hasBeenForciblyEnded;

  @Getter
  @Setter
  private boolean invalid;

  @Getter
  private long startTime;

  @Getter
  @Setter
  private long endTime;

  @Getter
  private int participantCount;

  @Getter
  private int listenerCount;

  @Getter
  private int voiceParticipantCount;

  @Getter
  private int videoCount;

  @Getter
  private int maxUsers;

  @Getter
  private int moderatorCount;

  @ToString.Exclude
  @XmlElementWrapper(name = "attendees")
  @XmlElement(name = "attendee")
  @Getter
  private List<Attendee> attendees;

  @Getter
  @ToString.Exclude
  private MeetingMetadata metadata;

  @XmlElement(name = "isBreakout")
  @Getter
  private boolean breakout;
}
