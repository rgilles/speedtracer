package org.speedtracer.collect;

/**
 * Interface for a trace collector.
 * 
 * @author Dustin
 * 
 */
public interface TraceCollector {

    public enum StepType {
		HTTP, METHOD, THREAD;
	}

	/**
	 * Begin a step.
	 * 
	 * @param label
	 *            description of the step
	 * @param type
	 *            the step type
	 * @param className
	 *            class name, if step is a method invocation
	 * @param methodName
	 *            method name, if step is a method invocation
	 * @param lineNumber
	 *            line number, if step is a method invocation
	 */
	void beginStep(String label, StepType type, String className,
			String methodName, long lineNumber);

	/**
	 * Begin a step.
	 * 
	 * @param label
	 *            description of the step
	 * @param type
	 *            the step type
	 */
	void beginStep(String label, StepType type);

    /**
     * Begin a step.
     *
     * @param step the step description
     */
    void beginStep(Step step);

	/**
	 * Complete a step.
	 */
	void completeStep();
}
