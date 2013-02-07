package org.speedtracer.perform;

/**
 * Interface for a component that can perform traces, usually implemented by an
 * Aspect.
 * 
 * @author Dustin
 * 
 */
public interface TracePerformer {

	/**
	 * Set whether the aspect is enabled on the current thread, for the fastest
	 * possible check without calling other services when disabled.
	 * 
	 * @param enabled
	 *            true if enabled
	 */
	void setEnabled(boolean enabled);

    Object trace(Interceptor interceptor) throws Throwable;
}