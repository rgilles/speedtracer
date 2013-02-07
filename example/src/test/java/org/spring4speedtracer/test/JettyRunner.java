package org.spring4speedtracer.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.speedtracer.filter.AbstractSpeedTracerFilter;

/**
 * Helper main class for launching the example application with tracing enabled
 * on Jetty from Eclipse or Ant.
 * 
 * @author Dustin
 * 
 */
public class JettyRunner {

	private static final int PORT = 9080;

	public static void main(String argv[]) throws Exception {

		// Enable tracing
		System.setProperty(AbstractSpeedTracerFilter.TRACE_ENABLED_PROPERTY,
				Boolean.TRUE.toString());

		// Create a Jetty server
		Server server = new Server(PORT);

		// Deploy war
		WebAppContext context = new WebAppContext("build/libs/example-1.0-SNAPSHOT.war",
				"/example");
		server.setHandler(context);

		// Start server
		server.start();
	}
}
