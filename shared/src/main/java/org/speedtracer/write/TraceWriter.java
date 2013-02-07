package org.speedtracer.write;

import java.io.IOException;
import java.io.Writer;

import org.speedtracer.collect.ServerTrace;

/**
 * Interface for a trace writer.
 * 
 * @author Dustin
 * 
 */
public interface TraceWriter {

	/**
	 * Write a trace in the implemented format.
	 * 
	 * @param trace
	 *            the trace
	 * @param writer
	 *            the writer
	 */
	void write(ServerTrace trace, Writer writer) throws IOException;
}
