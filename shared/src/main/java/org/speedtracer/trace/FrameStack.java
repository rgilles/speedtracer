package org.speedtracer.trace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Speed Tracer frame stack concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class FrameStack implements Serializable {

	private String id;
	private Range range;
	private Operation operation;
	private List<FrameStack> children = new ArrayList<FrameStack>();
	private transient FrameStack parent;
	private transient String threadName;

	public FrameStack() {
	}

	public FrameStack(String id, String label, String type, String className,
			String methodName, long lineNumber, FrameStack parent,
			String threadName) {
		this.id = id;
		this.range = new Range();
		this.operation = new Operation(label, type,
				className != null ? new SourceCodeLocation(className,
						methodName, lineNumber) : null);
		this.parent = parent;
		this.threadName = threadName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public List<FrameStack> getChildren() {
		return children;
	}

	public void setChildren(List<FrameStack> children) {
		this.children = children;
	}

	public FrameStack getParent() {
		return parent;
	}

	public String getThreadName() {
		return threadName;
	}

	public long getChildrenDuration() {
		int children = 0;
		for (FrameStack child : getChildren()) {
			if (getThreadName() == null && child.getThreadName() == null
					|| getThreadName() != null
					&& getThreadName().equals(child.getThreadName())) {
				children += child.getRange().getDuration();
			}
		}
		return children;
	}
}
