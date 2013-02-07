package org.speedtracer.collect;

/**
 * Interface for managing traces being collected across multiple threads.
 * 
 * @author Dustin
 * 
 */
public interface TraceManager extends TraceCollector {

	/**
	 * Get the active trace on the current thread, if any.
	 * 
	 * @return the trace
	 */
	ServerTrace getCurrentTrace();

	/**
	 * Set the active trace for the current thread, replacing any existing
	 * trace.
	 * 
	 * @param trace
	 *            the trace
	 */
	void setCurrentTrace(ServerTrace trace);

	/**
	 * Clear the active trace for the current thread.
	 */
	void clearCurrentTrace();
}
