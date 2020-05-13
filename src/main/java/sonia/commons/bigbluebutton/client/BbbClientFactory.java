package sonia.commons.bigbluebutton.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 *
 * @author Thosten Ludewig <t.ludewig@ostfalia.de>
 */
public class BbbClientFactory {
  
  private BbbClientFactory() {}
  
  public static BbbClient createClient( String apiUrl, String secret )
  {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(apiUrl);
    return new BbbClient( target, apiUrl.length(), secret );
  } 
}
