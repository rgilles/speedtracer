package org.speedtracer.trace;

public interface TraceHeader {

	/**
	 * Get the value for the id header.
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Get the value for the url header.
	 * 
	 * @return
	 */
	String getUrl();
}
