package org.speedtracer.collect;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.speedtracer.trace.Application;
import org.speedtracer.trace.FrameStack;
import org.speedtracer.trace.Resources;
import org.speedtracer.trace.Trace;

import static java.lang.String.valueOf;

/**
 * Data object for an entire HTTP server request trace.
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class ServerTrace implements Serializable {
    private static AtomicLong COUNTER = new AtomicLong();
	private Trace trace;

	private transient long frameId = 0;
	private transient ThreadLocal<FrameStack> current = new ThreadLocal<FrameStack>();
	private transient FrameStack anchor;
	private transient ThreadLocal<String> threadName = new ThreadLocal<String>();

	private transient ByteArrayOutputStream output;

	public ServerTrace(String name, String formattedName, String urlPrefix) {
		trace = new Trace();
//		trace.setId(UUID.randomUUID().toString());
		trace.setId(valueOf(COUNTER.incrementAndGet()));
		trace.setUrl(urlPrefix + trace.getId());
		trace.setApplication(new Application(name, formattedName));
		trace.setDate(System.currentTimeMillis());
		trace.setResources(new Resources(formattedName));
	}

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
	}

	public String getId() {
		return trace.getId();
	}

	public String getUrl() {
		return trace.getUrl();
	}

	/**
	 * Push a new frame onto the trace.
	 * 
	 * @param label
	 *            the label
	 * @param type
	 *            the type
	 * @param className
	 *            the class name (optional)
	 * @param methodName
	 *            the method name (optional)
	 * @param lineNumber
	 *            the line number (optional)
	 */
	public void push(String label, TraceCollector.StepType type, String className,
			String methodName, long lineNumber) {
		FrameStack currentFrame = current.get();
		if (currentFrame == null && anchor != null) {
			currentFrame = anchor;
		}
		FrameStack newFrame = new FrameStack(
				Long.valueOf(frameId++).toString(), label, type.toString(),
				className, methodName, lineNumber, currentFrame, threadName
						.get());
		if (currentFrame == null) {
			trace.setFrameStack(newFrame);
		} else {
			currentFrame.getChildren().add(newFrame);
		}
		current.set(newFrame);
	}

	/**
	 * Pop a frame from the trace.
	 */
	public void pop() {
		FrameStack currentFrame = current.get();
		currentFrame.getRange().finish();
		if (currentFrame.getParent() == null) {
			trace.setRange(currentFrame.getRange());
		}
		current.set(currentFrame.getParent());
	}

	/**
	 * Set the current frame as the anchor for subsequent frames on other
	 * threads.
	 */
	public void anchor() {
		anchor = current.get();
	}

	/**
	 * Set the formatted output.
	 * 
	 * @param output
	 *            the output
	 */
	public void setOutput(ByteArrayOutputStream output) {
		this.output = output;
	}

	/**
	 * Get the formatted output
	 * 
	 * @return the output
	 */
	public ByteArrayOutputStream getOutput() {
		return output;
	}

	/**
	 * Set the current thread name.
	 * 
	 * @param name
	 */
	public void setThreadName(String name) {
		threadName.set(name);
	}
}
