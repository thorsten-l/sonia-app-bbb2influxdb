package sonia.commons.bigbluebutton.client;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Thosten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlRootElement( name = "response" )
@ToString
public class MeetingsResponse {
  
  @XmlElementWrapper(name="meetings")
  @XmlElement( name = "meeting" ) 
  @Getter
  private List<Meeting> meetings;
  
  @XmlElement( name = "returncode" )
  @Getter
  private String returnCode;
}
