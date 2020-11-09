/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.app.bbb2influxdb.beanshell;

import bsh.CallStack;
import bsh.Interpreter;
import org.slf4j.LoggerFactory;
import sonia.app.bbb2influxdb.App;
import sonia.app.bbb2influxdb.config.Host;

/**
 *
 * @author th
 */
public class nodeSetOffline
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(nodeSetOffline.class.
    getName());

  public static void invoke(Interpreter env, CallStack callstack, String name, boolean offline)
  {
    LOGGER.debug("node {} set offline = {}", name, offline );
    
    for (Host host : App.getConfig().getHosts())
    {
      if ( host.getHostname().equalsIgnoreCase(name))
      {
        env.getOut().println( "node " + host.getHostname() + " set offline = " + offline );
        host.setOffline(offline);
        break;
      }
    }
  }  
}
