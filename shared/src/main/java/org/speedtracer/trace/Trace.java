package org.speedtracer.trace;

import java.io.Serializable;

/**
 * Speed Tracer trace concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class Trace implements Serializable, TraceHeader {

	private String id;
	private String url;
	private Application application;
	private long date;
	private Range range;
	private Resources resources;
	private FrameStack frameStack;

	public Trace() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Resources getResources() {
		return resources;
	}

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public FrameStack getFrameStack() {
		return frameStack;
	}

	public void setFrameStack(FrameStack frameStack) {
		this.frameStack = frameStack;
	}
}
