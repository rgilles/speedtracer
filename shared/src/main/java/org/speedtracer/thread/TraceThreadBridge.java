package org.speedtracer.thread;

/**
 * Interface for linking multiple threads to the same trace.
 * 
 * @author Dustin
 * 
 */
public interface TraceThreadBridge {

	/**
	 * Add the current thread as a slave that is aggregated to the master.
	 * 
	 * @param name
	 *            the thread name
	 */
	void addThread(String name);

	/**
	 * Remove the current thread from slaves that are aggregated.
	 */
	void removeThread();
}
