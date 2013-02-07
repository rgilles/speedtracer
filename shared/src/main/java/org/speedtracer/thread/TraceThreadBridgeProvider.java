package org.speedtracer.thread;

/**
 * Interface for providing a thread bridge that will link additional threads to
 * the current thread's trace.
 * 
 * @author Dustin
 * 
 */
public interface TraceThreadBridgeProvider {

	/**
	 * Get a thread bridge for joining traces from additional threads to the
	 * current thread.
	 * 
	 * @return the thread bridge
	 */
	TraceThreadBridge getTraceThreadBridge();
}
