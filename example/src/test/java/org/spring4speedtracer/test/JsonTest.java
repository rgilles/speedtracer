package org.spring4speedtracer.test;

import org.junit.Assert;
import org.junit.Test;
import org.speedtracer.collect.ServerTrace;
import org.speedtracer.collect.TraceCollector.StepType;

import com.google.gson.Gson;

/**
 * Test for writing a trace as JSON output.
 * 
 * @author Dustin
 * 
 */
public class JsonTest {

	@Test
	public void testServerTrace() {

		// Build object representation of a trace full of dummy data
		ServerTrace serverTrace = new ServerTrace("name", "formatted", "/url/");
		serverTrace.push("label0", StepType.METHOD, "className0",
				"methodName0", 0);
		serverTrace.pop();
		serverTrace.push("label1", StepType.METHOD, "className1",
				"methodName1", 1);
		serverTrace.push("label2", StepType.METHOD, "className2",
				"methodName2", 2);
		serverTrace.pop();
		serverTrace.pop();

		// Convert to json
		Gson gson = new Gson();
		String json = gson.toJson(serverTrace);

		// Very basic validation
		Assert.assertTrue(json.startsWith("{\"trace\":{\"id\":\""
				+ serverTrace.getId() + "\""));
		Assert.assertTrue(json.endsWith(",\"children\":[]}]}}}"));
		Assert.assertTrue(json.contains("\"url\":\"/url/" + serverTrace.getId()
				+ "\""));
	}
}
