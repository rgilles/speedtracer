package org.speedtracer.log.voter;

import org.speedtracer.collect.ServerTrace;

public interface TraceLogVoter {

	/**
	 * Set the config file.
	 * 
	 * @param configFile
	 *            the config file
	 */
	void setConfigFile(String configFile);

	/**
	 * Determine if a trace should be logged.
	 * 
	 * @param trace
	 *            the trace
	 */
	boolean shouldLog(ServerTrace trace);
}
