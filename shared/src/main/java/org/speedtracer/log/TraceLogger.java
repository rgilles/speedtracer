package org.speedtracer.log;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.speedtracer.collect.ServerTrace;

public interface TraceLogger {

	/**
	 * Log a trace using the implemented logger.
	 * 
	 * @param trace
	 *            the trace
	 */
	void log(ServerTrace trace, HttpServletRequest request) throws IOException;
}
