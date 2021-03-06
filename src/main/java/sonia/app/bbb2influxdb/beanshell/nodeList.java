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
public class nodeList
{
  final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(nodeList.class.
    getName());

  public static void invoke(Interpreter env, CallStack callstack)
  {
    LOGGER.debug("node list");
    for (Host host : App.getConfig().getHosts())
    {
      env.getOut().println( host.getHostname() + ", " + host.getApiUrl()
        + ", offline=" + host.isOffline());
    }
  }
}
