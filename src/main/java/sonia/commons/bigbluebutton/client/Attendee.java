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
public class Attendee {
  
  public boolean hasVideo()
  {
    return this.hasVideo;
  }
  
  public boolean hasJoinedVoice()
  {
    return this.hasJoinedVoice;
  }
  
  @Getter
  private String userID;
  
  @Getter
  private String fullName;
  
  @Getter
  private String role;
  
  @XmlElement( name = "isPresenter" )
  @Getter
  private boolean presenter;
  
  @XmlElement( name = "isListeningOnly" )
  @Getter
  private boolean listeningOnly;
    
  private boolean hasJoinedVoice;
  
  private boolean hasVideo;
  
  @Getter
  private String clientType;
}
