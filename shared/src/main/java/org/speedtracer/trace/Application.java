package org.speedtracer.trace;

import java.io.Serializable;

/**
 * Speed Tracer application concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class Application implements Serializable {

	private String name;
	private String formatted;

	public Application() {
	}

	public Application(String name, String formatted) {
		this.name = name;
		this.formatted = formatted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormatted() {
		return formatted;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}
}
