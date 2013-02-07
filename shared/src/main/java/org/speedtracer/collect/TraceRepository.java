package org.speedtracer.collect;

import org.speedtracer.trace.TraceHeader;

import java.util.Map;

/**
 * Interface for a trace repository.
 * 
 * @author Dustin
 * 
 */
public interface TraceRepository extends TraceCollector {

	/**
	 * Set the max number of traces to retain.
	 * 
	 * @param maxTraces
	 *            the max number to keep
	 */
	void setMaxTraces(int maxTraces);

	/**
	 * Begin tracing on the current thread, also clearing any existing traces.
	 * 
	 * @param name
	 *            the trace name
	 * @param formattedName
	 *            the trace formatted name
	 * @param urlPrefix
	 *            the url prefix
	 * @return the header data for the trace
	 */
	TraceHeader beginTrace(String name, String formattedName, String urlPrefix);

	/**
	 * Complete tracing on the current thread, saving the results.
	 */
	void completeTrace();

	/**
	 * Get a trace by id.
	 * 
	 * @param id
	 *            the id
	 * @return the trace
	 */
	ServerTrace getTrace(String id);

	/**
	 * Get a map of all traces by id (not modifiable).
	 * 
	 * @return the map of traces
	 */
	Map<String, ServerTrace> getTraces();
}
