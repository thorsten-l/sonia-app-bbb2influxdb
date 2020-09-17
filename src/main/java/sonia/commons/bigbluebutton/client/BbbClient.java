package sonia.commons.bigbluebutton.client;

import java.util.List;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>Thosten Ludewig <t.ludewig@ostfalia.de>
 */
public class BbbClient {
  
  private final int apiUrlLength;
  private final String secret;
  private final WebTarget target;
  
  BbbClient( WebTarget target, int apiUrlLength, String secret )
  {
    this.target = target;
    this.apiUrlLength = apiUrlLength;
    this.secret = secret;
  }
  
  private WebTarget appendChecksum( WebTarget target )
  {
    String uri = target.getUri().toString().substring(apiUrlLength);
    
    int i = uri.indexOf('?');
    
    if ( i > 0 )
    {
      uri = uri.substring(0, i) + uri.substring(i+1);
    }
        
    return target.queryParam("checksum", DigestUtils.sha1Hex( uri + secret ));
  }
  
  public List<Meeting> getMeetings()
  {
    MeetingsResponse response = appendChecksum(target.path("getMeetings"))
            .request().accept(MediaType.APPLICATION_XML).get(MeetingsResponse.class);
        
    return response.getMeetings();
  }

  public MeetingResponse getMeetingInfo( String meetingID )
  {
    return appendChecksum(target.path("getMeetingInfo").queryParam("meetingID", meetingID))
            .request().accept(MediaType.APPLICATION_XML).get(MeetingResponse.class);    
  }

  public List<Recording> getRecordings()
  {
    RecordingsResponse response = appendChecksum(target.path("getRecordings"))
            .request().accept(MediaType.APPLICATION_XML).get(RecordingsResponse.class);
    return response.getRecordings();
  }
  
  public Statistics getStatistics(String hostname)
  {
    return new Statistics(getMeetings(), hostname);
  }
}
