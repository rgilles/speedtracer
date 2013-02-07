package org.speedtracer.trace;

import java.io.Serializable;

/**
 * Speed Tracer source code location concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class SourceCodeLocation implements Serializable {

	private String className;
	private String methodName;
	private long lineNumber;

	public SourceCodeLocation() {
	}

	public SourceCodeLocation(String className, String methodName,
			long lineNumber) {
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}
}
