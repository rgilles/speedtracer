package org.speedtracer.write;

import java.io.IOException;
import java.io.Writer;

import org.speedtracer.collect.ServerTrace;

import com.google.gson.Gson;

/**
 * Trace writer that outputs JSON for consumption by Speed Tracer.
 * 
 * @author Dustin
 * 
 */
public class JsonTraceWriter implements TraceWriter {

	private Gson gson = new Gson();

	@Override
	public void write(ServerTrace trace, Writer writer) throws IOException {
		gson.toJson(trace, writer);
		writer.flush();
	}
}
