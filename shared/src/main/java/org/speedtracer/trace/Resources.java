package org.speedtracer.trace;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Speed Tracer resources concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class Resources implements Serializable {

	@SerializedName("Application")
	private String application;

	public Resources(String application) {
		this.application = application;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
