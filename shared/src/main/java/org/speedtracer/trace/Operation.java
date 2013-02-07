package org.speedtracer.trace;

import java.io.Serializable;

/**
 * Speed Tracer operation concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class Operation implements Serializable {

	private String label;
	private String type;
	private SourceCodeLocation sourceCodeLocation;

	public Operation() {
	}

	public Operation(String label, String type,
			SourceCodeLocation sourceCodeLocation) {
		this.label = label;
		this.type = type;
		this.sourceCodeLocation = sourceCodeLocation;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SourceCodeLocation getSourceCodeLocation() {
		return sourceCodeLocation;
	}

	public void setSourceCodeLocation(SourceCodeLocation sourceCodeLocation) {
		this.sourceCodeLocation = sourceCodeLocation;
	}
}
